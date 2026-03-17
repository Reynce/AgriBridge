package com.reyn.service;


import com.reyn.objects.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    /**
     * 保存聊天消息
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
     * 获取用户与客服的聊天历史
     * @param userId 用户ID
     * @param serviceId 客服ID
     * @return 聊天历史记录
     */
    List<ChatMessage> getChatHistory(String userId, String serviceId);

    /**
     * 将用户的所有未读消息标记为已读
     * @param receiverId 接收者ID
     */
    void markMessagesAsRead(String receiverId);
}