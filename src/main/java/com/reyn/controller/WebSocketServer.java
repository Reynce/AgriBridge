package com.reyn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reyn.objects.entity.ChatMessage;
import com.reyn.service.ChatMessageService;
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
@ServerEndpoint("/websocket/{userId}/{userType}") // userType: user(用户) 或 service(客服)
public class WebSocketServer {

    // 存储所有连接的会话
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();
    private static final Map<String, Session> serviceSessions = new ConcurrentHashMap<>();
    
    // 用户与客服的配对关系
    private static final Map<String, String> userServiceMapping = new ConcurrentHashMap<>();
    
    // 等待服务的用户队列
    private static final List<String> waitingUsers = new ArrayList<>();
    
    // 自动分配的客服ID
    private static int currentServiceId = 1;
    
    private static ChatMessageService chatMessageService;
    
    @Autowired
    public void setChatMessageService(ChatMessageService chatMessageService) {
        WebSocketServer.chatMessageService = chatMessageService;
    }
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 客户端连接时触发
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId, @PathParam("userType") String userType) {
        System.out.println("客户端连接成功: " + userId + " 类型: " + userType);
        
        if ("service".equals(userType)) {
            // 客服连接
            serviceSessions.put(userId, session);
            System.out.println("客服 " + userId + " 已连接");
            
            // 检查是否有等待的用户
            assignWaitingUsersToService(userId);
            
        } else {
            // 用户连接
            userSessions.put(userId, session);
            System.out.println("用户 " + userId + " 已连接");
            
            // 自动分配客服
            assignServiceToUser(userId);
        }
    }

    // 接收客户端消息
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String userId, @PathParam("userType") String userType) throws JsonProcessingException {
        System.out.println("收到消息: " + message + " 来自: " + userId + " 类型: " + userType);

        // 解析消息
        MessageData messageData = objectMapper.readValue(message, MessageData.class);

        if ("service".equals(userType)) {
            if (messageData.getMessageType().equals("system") && messageData.getContent().equals("get_history")) {
                // 处理获取历史消息的请求
                handleHistoryRequest(userId);
            } else {
                // 客服发送消息给用户
                handleServiceMessage(userId, messageData);
            }
        } else {
            if (messageData.getMessageType().equals("system") && messageData.getContent().equals("get_history")) {
                // 处理获取历史消息的请求
                String serviceId = userServiceMapping.get(userId);
                if (serviceId != null) {
                    handleHistoryRequest(userId, serviceId);
                }
            } else {
                // 用户发送消息给客服
                handleUserMessage(userId, messageData);
            }
        }
    }

    // 连接关闭时触发
    @OnClose
    public void onClose(@PathParam("userId") String userId, @PathParam("userType") String userType) {
        if ("service".equals(userType)) {
            serviceSessions.remove(userId);
            System.out.println("客服 " + userId + " 连接关闭");
            
            // 重新分配该客服的用户
            reassignUsersFromService(userId);
        } else {
            userSessions.remove(userId);
            System.out.println("用户 " + userId + " 连接关闭");
            
            // 从等待队列中移除
            waitingUsers.remove(userId);
            
            // 清理配对关系
            String serviceId = userServiceMapping.remove(userId);
            if (serviceId != null) {
                // 通知客服用户已断开
                notifyServiceUserDisconnected(serviceId, userId);
            }
        }
    }

    // 发生错误时触发
    @OnError
    public void onError(Throwable error, @PathParam("userId") String userId) {
        System.err.println("WebSocket错误 - 用户: " + userId);
        error.printStackTrace();
    }

    // 处理用户消息
    private void handleUserMessage(String userId, MessageData messageData) {
        String serviceId = userServiceMapping.get(userId);
        
        if (serviceId == null) {
            // 用户还没有分配客服，加入等待队列
            if (!waitingUsers.contains(userId)) {
                waitingUsers.add(userId);
            }
            
            // 发送等待消息给用户
            sendMessageToUser(userId, new MessageData("system", "正在为您分配客服，请稍候...", "system"));
            return;
        }
        
        // 转发消息给对应的客服
        sendMessageToService(serviceId, new MessageData(userId, messageData.getContent(), "user"));
        
        // 保存消息到数据库
        saveMessage(userId, serviceId, messageData.getContent(), "user");
    }

    // 处理客服消息
    private void handleServiceMessage(String serviceId, MessageData messageData) {
        String targetUserId = messageData.getTargetUserId();
        
        if (targetUserId != null) {
            // 发送消息给指定用户
            sendMessageToUser(targetUserId, new MessageData(serviceId, messageData.getContent(), "service"));
            
            // 保存消息到数据库
            saveMessage(serviceId, targetUserId, messageData.getContent(), "service");
        }
    }

    // 为用户分配客服
    private void assignServiceToUser(String userId) {
        // 查找可用的客服
        String availableServiceId = findAvailableService();
        
        if (availableServiceId != null) {
            userServiceMapping.put(userId, availableServiceId);
            
            // 通知客服有新用户
            sendMessageToService(availableServiceId, new MessageData(userId, "新用户连接", "system"));
            
            // 通知用户已分配客服
            sendMessageToUser(userId, new MessageData(availableServiceId, "客服已为您服务", "system"));
            
        } else {
            // 没有可用客服，加入等待队列
            if (!waitingUsers.contains(userId)) {
                waitingUsers.add(userId);
            }
            sendMessageToUser(userId, new MessageData("system", "当前客服繁忙，请稍候...", "system"));
        }
    }

    // 查找可用客服
    private String findAvailableService() {
        for (String serviceId : serviceSessions.keySet()) {
            // 简单的负载均衡：检查该客服当前服务的用户数量
            long userCount = userServiceMapping.values().stream()
                    .filter(id -> id.equals(serviceId))
                    .count();
            
            if (userCount < 5) { // 每个客服最多服务5个用户
                return serviceId;
            }
        }
        return null;
    }

    // 为客服分配等待的用户
    private void assignWaitingUsersToService(String serviceId) {
        if (waitingUsers.isEmpty()) {
            return;
        }
        
        // 分配最多3个等待用户给新连接的客服
        int assignedCount = 0;
        for (int i = 0; i < Math.min(3, waitingUsers.size()); i++) {
            String userId = waitingUsers.get(i);
            if (userSessions.containsKey(userId)) {
                userServiceMapping.put(userId, serviceId);
                sendMessageToUser(userId, new MessageData(serviceId, "客服已为您服务", "system"));
                assignedCount++;
            }
        }
        
        // 从等待队列中移除已分配的用户
        for (int i = 0; i < assignedCount; i++) {
            if (!waitingUsers.isEmpty()) {
                waitingUsers.remove(0);
            }
        }
    }

    // 重新分配客服断开连接的用户
    private void reassignUsersFromService(String serviceId) {
        List<String> orphanedUsers = new ArrayList<>();
        
        // 找出该客服服务的所有用户
        for (Map.Entry<String, String> entry : userServiceMapping.entrySet()) {
            if (serviceId.equals(entry.getValue())) {
                orphanedUsers.add(entry.getKey());
            }
        }
        
        // 重新分配这些用户
        for (String userId : orphanedUsers) {
            userServiceMapping.remove(userId);
            assignServiceToUser(userId);
        }
    }

    // 通知客服用户断开连接
    private void notifyServiceUserDisconnected(String serviceId, String userId) {
        sendMessageToService(serviceId, new MessageData(userId, "用户已断开连接", "system"));
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

    // 发送消息给客服
    private void sendMessageToService(String serviceId, MessageData messageData) {
        Session session = serviceSessions.get(serviceId);
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(objectMapper.writeValueAsString(messageData));
            } catch (Exception e) {
                System.err.println("发送消息给客服失败: " + e.getMessage());
            }
        }
    }

    // 保存消息到数据库
    // 处理客服的历史消息请求
    private void handleHistoryRequest(String serviceId) {
        // 获取该客服服务的所有用户
        List<String> serviceUsers = new ArrayList<>();
        for (Map.Entry<String, String> entry : userServiceMapping.entrySet()) {
            if (serviceId.equals(entry.getValue())) {
                serviceUsers.add(entry.getKey());
            }
        }

        // 获取每个用户的聊天历史
        for (String userId : serviceUsers) {
            List<ChatMessage> history = chatMessageService.getChatHistory(userId, serviceId);
            if (!history.isEmpty()) {
                MessageData historyMessage = new MessageData();
                historyMessage.setSenderId(userId);
                historyMessage.setMessageType("system");
                historyMessage.setContent("history_messages");
                historyMessage.setHistory(history);
                sendMessageToService(serviceId, historyMessage);
            }
        }
    }

    // 处理用户的历史消息请求
    private void handleHistoryRequest(String userId, String serviceId) {
        List<ChatMessage> history = chatMessageService.getChatHistory(userId, serviceId);
        if (!history.isEmpty()) {
            MessageData historyMessage = new MessageData();
            historyMessage.setSenderId(serviceId);
            historyMessage.setMessageType("system");
            historyMessage.setContent("history_messages");
            historyMessage.setHistory(history);
            sendMessageToUser(userId, historyMessage);
        }
        // 标记消息为已读
        chatMessageService.markMessagesAsRead(userId);
    }

    private void saveMessage(String senderId, String receiverId, String content, String messageType) {
        try {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderId(senderId);
            chatMessage.setReceiverId(receiverId);
            chatMessage.setContent(content);
            chatMessage.setCreateTime(LocalDateTime.now());
            chatMessage.setIsRead(false);
            
            if (chatMessageService != null) {
                chatMessageService.saveMessage(chatMessage);
            }
        } catch (Exception e) {
            System.err.println("保存消息失败: " + e.getMessage());
        }
    }

    // 消息数据类
    public static class MessageData {
        private String senderId;
        private String targetUserId;
        private String content;
        private String messageType;
        private String timestamp;
        private List<ChatMessage> history;

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
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public List<ChatMessage> getHistory() { return history; }
        public void setHistory(List<ChatMessage> history) { this.history = history; }
    }
}