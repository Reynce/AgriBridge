package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.entity.ChatMessage;
import com.reyn.service.MerchantChatService;
import com.reyn.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant-chat")
public class MerchantChatController {

    @Autowired
    private MerchantChatService merchantChatService;

    /**
     * 获取用户与指定商家的聊天历史
     */
    @GetMapping("/history/{merchantId}")
    public SaResult getChatHistory(@PathVariable Long merchantId) {
        Long userId = LoginHelper.getLoginUserId();
        List<ChatMessage> history = merchantChatService.getChatHistory(userId.toString(), merchantId);
        return SaResult.ok("获取聊天历史成功").setData(history);
    }

    /**
     * 获取商家的所有未处理对话列表
     */
    @GetMapping("/conversations")
    public SaResult getActiveConversations() {
        Long merchantId = LoginHelper.getLoginUserId(); // 假设商家也是通过登录获取ID
        List<String> conversations = merchantChatService.getActiveConversations(merchantId);
        return SaResult.ok("获取活跃对话列表成功").setData(conversations);
    }

    /**
     * 标记用户消息为已读
     */
    @PostMapping("/mark-read/{userId}")
    public SaResult markMessagesAsRead(@PathVariable String userId) {
        merchantChatService.markMessagesAsRead(userId);
        return SaResult.ok("消息标记为已读成功");
    }

    /**
     * 发送消息给用户
     */
    @PostMapping("/send")
    public SaResult sendMessage(@RequestBody SendMessageDTO messageDTO) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId("merchant_" + LoginHelper.getLoginUserId());
        chatMessage.setReceiverId(messageDTO.getUserId());
        chatMessage.setContent(messageDTO.getContent());

        ChatMessage savedMessage = merchantChatService.saveMessage(chatMessage);
        return SaResult.ok("消息发送成功").setData(savedMessage);
    }

    // 消息发送数据传输对象
    public static class SendMessageDTO {
        private String userId;
        private String content;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
