package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MerchantChatMapper extends BaseMapper<ChatMessage> {

    @Select("SELECT * FROM chat_message WHERE receiver_id LIKE 'merchant_${merchantId}%' AND is_read = false GROUP BY sender_id")
    List<ChatMessage> selectActiveConversations(@Param("merchantId") Long merchantId);

    @Select("SELECT * FROM chat_message WHERE (sender_id = #{userId} AND receiver_id = 'merchant_${merchantId}') OR (sender_id = 'merchant_${merchantId}' AND receiver_id = #{userId}) ORDER BY create_time ASC")
    List<ChatMessage> selectMerchantChatHistory(@Param("userId") String userId, @Param("merchantId") Long merchantId);

    @Update("UPDATE chat_message SET is_read = true WHERE receiver_id = #{receiverId} AND is_read = false")
    void updateMessagesReadStatus(@Param("receiverId") String receiverId);
}
