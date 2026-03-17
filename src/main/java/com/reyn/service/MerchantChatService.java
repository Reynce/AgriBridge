package com.reyn.service;

import com.reyn.objects.entity.ChatMessage;

import java.util.List;

public interface MerchantChatService {
    /**
     * 保存商家聊天消息
     * @param chatMessage 消息实体
     * @return 保存后的消息实体（可能包含数据库生成的ID和时间戳）
     */
    ChatMessage saveMessage(ChatMessage chatMessage);

    /**
     * 获取用户的未读消息
     * @param receiverId 接收者ID
     * @return 未读消息列表
     */
    List<ChatMessage> getUnreadMessages(String receiverId);

    /**
     * 获取用户与指定商家的聊天历史
     * @param userId 用户ID
     * @param merchantId 商家ID
     * @return 聊天历史记录
     */
    List<ChatMessage> getChatHistory(String userId, Long merchantId);

    /**
     * 将用户的所有未读消息标记为已读
     * @param receiverId 接收者ID
     */
    void markMessagesAsRead(String receiverId);

    /**
     * 获取商家的所有未处理对话列表
     * @param merchantId 商家ID
     * @return 用户ID列表
     */
    List<String> getActiveConversations(Long merchantId);
}
