package com.reyn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reyn.objects.entity.ChatMessage;
import com.reyn.service.MerchantChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/merchant-websocket/{userId}/{userType}/{merchantId}")
public class MerchantWebSocketServer {

    // 存储所有连接的会话
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();
    private static final Map<String, Session> merchantSessions = new ConcurrentHashMap<>();

    // 用户与商家的配对关系 (userId -> merchantId)
    private static final Map<String, Long> userMerchantMapping = new ConcurrentHashMap<>();

    // 等待服务的用户队列
    private static final Map<Long, List<String>> waitingUsersByMerchant = new ConcurrentHashMap<>();

    private static MerchantChatService merchantChatService;

    @Autowired
    public void setMerchantChatService(MerchantChatService merchantChatService) {
        MerchantWebSocketServer.merchantChatService = merchantChatService;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 客户端连接时触发
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId,
                       @PathParam("userType") String userType, @PathParam("merchantId") Long merchantId) {
        System.out.println("客户端连接成功: " + userId + " 类型: " + userType + " 商家ID: " + merchantId);

        if ("merchant".equals(userType)) {
            // 商家连接
            String merchantKey = merchantId.toString();
            merchantSessions.put(merchantKey, session);
            System.out.println("商家 " + merchantId + " 已连接");

            // 检查是否有等待的用户
            assignWaitingUsersToMerchant(merchantId);

        } else {
            // 用户连接
            userSessions.put(userId, session);
            userMerchantMapping.put(userId, merchantId);
            System.out.println("用户 " + userId + " 已连接到商家 " + merchantId);

            // 自动分配商家客服
            assignMerchantToUser(userId, merchantId);
        }
    }

    // 接收客户端消息
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String userId,
                          @PathParam("userType") String userType, @PathParam("merchantId") Long merchantId)
            throws JsonProcessingException {
        System.out.println("收到消息: " + message + " 来自: " + userId + " 类型: " + userType + " 商家ID: " + merchantId);

        // 解析消息
        MessageData messageData = objectMapper.readValue(message, MessageData.class);

        if ("merchant".equals(userType)) {
            if (messageData.getMessageType().equals("system") && messageData.getContent().equals("get_history")) {
                // 处理获取历史消息的请求
                handleMerchantHistoryRequest(merchantId);
            } else if (messageData.getMessageType().equals("system") && messageData.getContent().equals("get_active_users")) {
                // 处理获取活跃用户列表的请求
                handleActiveUsersRequest(merchantId);
            } else {
                // 商家发送消息给用户
                handleMerchantMessage(merchantId, messageData);
            }
        } else {
            if (messageData.getMessageType().equals("system") && messageData.getContent().equals("get_history")) {
                // 处理获取历史消息的请求
                handleUserHistoryRequest(userId, merchantId);
            } else {
                // 用户发送消息给商家
                handleUserMessage(userId, messageData);
            }
        }
    }

    // 连接关闭时触发
    @OnClose
    public void onClose(@PathParam("userId") String userId, @PathParam("userType") String userType,
                        @PathParam("merchantId") Long merchantId) {
        if ("merchant".equals(userType)) {
            merchantSessions.remove(merchantId.toString());
            System.out.println("商家 " + merchantId + " 连接关闭");

            // 重新分配该商家的用户
            reassignUsersFromMerchant(merchantId);
        } else {
            userSessions.remove(userId);
            userMerchantMapping.remove(userId);
            System.out.println("用户 " + userId + " 连接关闭");

            // 从等待队列中移除
            removeFromWaitingQueue(userId, merchantId);

            // 通知商家用户已断开
            notifyMerchantUserDisconnected(merchantId, userId);
        }
    }

    // 发生错误时触发
    @OnError
    public void onError(Throwable error, @PathParam("userId") String userId) {
        System.err.println("商家WebSocket错误 - 用户: " + userId);
        error.printStackTrace();
    }

    // 处理用户消息
    private void handleUserMessage(String userId, MessageData messageData) {
        Long merchantId = userMerchantMapping.get(userId);

        if (merchantId == null) {
            // 用户还没有分配商家，加入等待队列
            addToWaitingQueue(userId, messageData.getTargetMerchantId());
            sendMessageToUser(userId, new MessageData("system", "正在为您联系商家，请稍候...", "system"));
            return;
        }

        String merchantIdStr = "merchant_" + merchantId;

        // 转发消息给对应的商家
        sendMessageToMerchant(merchantId, new MessageData(userId, messageData.getContent(), "user"));

        // 保存消息到数据库
        saveMessage(userId, merchantIdStr, messageData.getContent(), "user");
    }

    // 处理商家消息
    private void handleMerchantMessage(Long merchantId, MessageData messageData) {
        String targetUserId = messageData.getTargetUserId();

        if (targetUserId != null) {
            // 发送消息给指定用户
            String merchantIdStr = "merchant_" + merchantId;
            sendMessageToUser(targetUserId, new MessageData(merchantIdStr, messageData.getContent(), "merchant"));

            // 保存消息到数据库
            saveMessage(merchantIdStr, targetUserId, messageData.getContent(), "merchant");
        }
    }

    // 为用户分配商家
    private void assignMerchantToUser(String userId, Long merchantId) {
        // 检查商家是否在线
        if (merchantSessions.containsKey(merchantId.toString())) {
            userMerchantMapping.put(userId, merchantId);

            // 通知商家有新用户
            sendMessageToMerchant(merchantId, new MessageData(userId, "新用户连接", "system"));

            // 通知用户已连接到商家
            sendMessageToUser(userId, new MessageData("merchant_" + merchantId, "商家客服已为您服务", "system"));

        } else {
            // 商家不在线，加入等待队列
            addToWaitingQueue(userId, merchantId);
            sendMessageToUser(userId, new MessageData("system", "商家暂时不在线，请稍候...", "system"));
        }
    }

    // 为商家分配等待的用户
    private void assignWaitingUsersToMerchant(Long merchantId) {
        List<String> waitingUsers = waitingUsersByMerchant.get(merchantId);
        if (waitingUsers == null || waitingUsers.isEmpty()) {
            return;
        }

        // 分配最多3个等待用户给新连接的商家
        int assignedCount = 0;
        List<String> usersToRemove = new ArrayList<>();

        for (String userId : waitingUsers) {
            if (userSessions.containsKey(userId) && assignedCount < 3) {
                userMerchantMapping.put(userId, merchantId);
                sendMessageToUser(userId, new MessageData("merchant_" + merchantId, "商家客服已为您服务", "system"));
                usersToRemove.add(userId);
                assignedCount++;
            }
        }

        // 从等待队列中移除已分配的用户
        waitingUsers.removeAll(usersToRemove);
        if (waitingUsers.isEmpty()) {
            waitingUsersByMerchant.remove(merchantId);
        }
    }

    // 重新分配商家断开连接的用户
    private void reassignUsersFromMerchant(Long merchantId) {
        List<String> orphanedUsers = new ArrayList<>();

        // 找出该商家服务的所有用户
        for (Map.Entry<String, Long> entry : userMerchantMapping.entrySet()) {
            if (merchantId.equals(entry.getValue())) {
                orphanedUsers.add(entry.getKey());
            }
        }

        // 重新分配这些用户
        for (String userId : orphanedUsers) {
            userMerchantMapping.remove(userId);
            // 可以选择重新分配给其他在线商家或者加入通用等待队列
            sendMessageToUser(userId, new MessageData("system", "商家已离线，正在为您重新分配...", "system"));
        }
    }

    // 添加用户到等待队列
    private void addToWaitingQueue(String userId, Long merchantId) {
        waitingUsersByMerchant.computeIfAbsent(merchantId, k -> new ArrayList<>());
        if (!waitingUsersByMerchant.get(merchantId).contains(userId)) {
            waitingUsersByMerchant.get(merchantId).add(userId);
        }
    }

    // 从等待队列中移除用户
    private void removeFromWaitingQueue(String userId, Long merchantId) {
        List<String> waitingUsers = waitingUsersByMerchant.get(merchantId);
        if (waitingUsers != null) {
            waitingUsers.remove(userId);
            if (waitingUsers.isEmpty()) {
                waitingUsersByMerchant.remove(merchantId);
            }
        }
    }

    // 通知商家用户断开连接
    private void notifyMerchantUserDisconnected(Long merchantId, String userId) {
        sendMessageToMerchant(merchantId, new MessageData(userId, "用户已断开连接", "system"));
    }

    // 发送消息给用户
    private void sendMessageToUser(String userId, MessageData messageData) {
        Session session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(objectMapper.writeValueAsString(messageData));
            } catch (Exception e) {
                System.err.println("发送消息给用户失败: " + e.getMessage());
            }
        }
    }

    // 发送消息给商家
    private void sendMessageToMerchant(Long merchantId, MessageData messageData) {
        Session session = merchantSessions.get(merchantId.toString());
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(objectMapper.writeValueAsString(messageData));
            } catch (Exception e) {
                System.err.println("发送消息给商家失败: " + e.getMessage());
            }
        }
    }

    // 处理商家的历史消息请求
    private void handleMerchantHistoryRequest(Long merchantId) {
        // 获取与该商家相关的所有用户
        List<String> merchantUsers = new ArrayList<>();
        for (Map.Entry<String, Long> entry : userMerchantMapping.entrySet()) {
            if (merchantId.equals(entry.getValue())) {
                merchantUsers.add(entry.getKey());
            }
        }

        // 获取每个用户的聊天历史
        for (String userId : merchantUsers) {
            List<ChatMessage> history = merchantChatService.getChatHistory(userId, merchantId);
            if (!history.isEmpty()) {
                MessageData historyMessage = new MessageData();
                historyMessage.setSenderId(userId);
                historyMessage.setMessageType("system");
                historyMessage.setContent("history_messages");
                historyMessage.setHistory(history);
                sendMessageToMerchant(merchantId, historyMessage);
            }
        }
    }

    // 处理用户的歷史消息請求
    private void handleUserHistoryRequest(String userId, Long merchantId) {
        List<ChatMessage> history = merchantChatService.getChatHistory(userId, merchantId);
        if (!history.isEmpty()) {
            MessageData historyMessage = new MessageData();
            historyMessage.setSenderId("merchant_" + merchantId);
            historyMessage.setMessageType("system");
            historyMessage.setContent("history_messages");
            historyMessage.setHistory(history);
            sendMessageToUser(userId, historyMessage);
        }
        // 标记消息为已读
        merchantChatService.markMessagesAsRead(userId);
    }

    // 处理活跃用户列表请求
    private void handleActiveUsersRequest(Long merchantId) {
        List<String> activeUsers = merchantChatService.getActiveConversations(merchantId);
        MessageData activeUsersMessage = new MessageData();
        activeUsersMessage.setSenderId("system");
        activeUsersMessage.setMessageType("system");
        activeUsersMessage.setContent("active_users");
        activeUsersMessage.setActiveUsers(activeUsers);
        sendMessageToMerchant(merchantId, activeUsersMessage);
    }

    // 保存消息到数据库
    private void saveMessage(String senderId, String receiverId, String content, String messageType) {
        try {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderId(senderId);
            chatMessage.setReceiverId(receiverId);
            chatMessage.setContent(content);
            chatMessage.setCreateTime(LocalDateTime.now());
            chatMessage.setIsRead(false);

            if (merchantChatService != null) {
                merchantChatService.saveMessage(chatMessage);
            }
        } catch (Exception e) {
            System.err.println("保存消息失败: " + e.getMessage());
        }
    }

    // 消息数据类
    public static class MessageData {
        private String senderId;
        private String targetUserId;
        private Long targetMerchantId;
        private String content;
        private String messageType;
        private String timestamp;
        private List<ChatMessage> history;
        private List<String> activeUsers;

        public MessageData() {}

        public MessageData(String senderId, String content, String messageType) {
            this.senderId = senderId;
            this.content = content;
            this.messageType = messageType;
            this.timestamp = LocalDateTime.now().toString();
        }

        // Getters and Setters
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }

        public String getTargetUserId() { return targetUserId; }
        public void setTargetUserId(String targetUserId) { this.targetUserId = targetUserId; }

        public Long getTargetMerchantId() { return targetMerchantId; }
        public void setTargetMerchantId(Long targetMerchantId) { this.targetMerchantId = targetMerchantId; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public List<ChatMessage> getHistory() { return history; }
        public void setHistory(List<ChatMessage> history) { this.history = history; }

        public List<String> getActiveUsers() { return activeUsers; }
        public void setActiveUsers(List<String> activeUsers) { this.activeUsers = activeUsers; }
    }
}
