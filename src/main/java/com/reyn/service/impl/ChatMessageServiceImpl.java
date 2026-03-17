package com.reyn.service.impl;

import com.reyn.mapper.ChatMessageMapper;
import com.reyn.objects.entity.ChatMessage;
import com.reyn.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    @Transactional
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        // 设置服务器时间为消息创建时间
        chatMessage.setCreateTime(LocalDateTime.now());
        // 默认设置为未读
        chatMessage.setIsRead(false);
        
        chatMessageMapper.insert(chatMessage);
        
        return chatMessage;
    }

    @Override
    public List<ChatMessage> getUnreadMessages(String receiverId) {
        return chatMessageMapper.selectUnreadMessages(receiverId);
    }

    @Override
    public List<ChatMessage> getChatHistory(String userId, String serviceId) {
        return chatMessageMapper.selectChatHistory(userId, serviceId);
    }

    @Override
    @Transactional
    public void markMessagesAsRead(String receiverId) {
        chatMessageMapper.updateMessagesReadStatus(receiverId);
    }
}