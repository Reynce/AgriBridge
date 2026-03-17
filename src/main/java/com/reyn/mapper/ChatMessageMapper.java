package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    @Select("SELECT * FROM chat_message WHERE receiver_id = #{receiverId} AND is_read = false ORDER BY create_time ASC")
    List<ChatMessage> selectUnreadMessages(@Param("receiverId") String receiverId);

    @Select("SELECT * FROM chat_message WHERE (sender_id = #{userId} AND receiver_id = #{serviceId}) OR (sender_id = #{serviceId} AND receiver_id = #{userId}) ORDER BY create_time ASC")
    List<ChatMessage> selectChatHistory(@Param("userId") String userId, @Param("serviceId") String serviceId);

    @Update("UPDATE chat_message SET is_read = true WHERE receiver_id = #{receiverId} AND is_read = false")
    void updateMessagesReadStatus(@Param("receiverId") String receiverId);
}