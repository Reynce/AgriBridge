package com.reyn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reyn.mapper.ChatMessageMapper;
import com.reyn.objects.entity.ChatMessage;
import com.reyn.service.MerchantChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MerchantChatServiceImpl implements MerchantChatService {

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
    public List<ChatMessage> getChatHistory(String userId, Long merchantId) {
        // 将merchantId转换为字符串格式进行查询
        String merchantIdStr = "merchant_" + merchantId;

        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(ChatMessage::getSenderId, userId).eq(ChatMessage::getReceiverId, merchantIdStr))
                .or(w -> w.eq(ChatMessage::getSenderId, merchantIdStr).eq(ChatMessage::getReceiverId, userId))
                .orderByAsc(ChatMessage::getCreateTime);

        return chatMessageMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void markMessagesAsRead(String receiverId) {
        chatMessageMapper.updateMessagesReadStatus(receiverId);
    }

    @Override
    public List<String> getActiveConversations(Long merchantId) {
        String merchantIdStr = "merchant_" + merchantId;

        // 查询与该商家相关的所有未读消息的发送者
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getReceiverId, merchantIdStr)
                .eq(ChatMessage::getIsRead, false)
                .select(ChatMessage::getSenderId)
                .groupBy(ChatMessage::getSenderId);

        List<ChatMessage> messages = chatMessageMapper.selectList(wrapper);
        return messages.stream()
                .map(ChatMessage::getSenderId)
                .distinct()
                .collect(Collectors.toList());
    }
}
