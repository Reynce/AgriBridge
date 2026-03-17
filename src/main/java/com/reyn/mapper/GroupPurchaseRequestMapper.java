package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.GroupPurchaseRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GroupPurchaseRequestMapper extends BaseMapper<GroupPurchaseRequest> {

    /**
     * 根据状态查询求购请求列表
     */
    @Select("SELECT * FROM group_purchase_request WHERE status = #{status} ORDER BY created_at DESC")
    List<GroupPurchaseRequest> selectByStatus(@Param("status") Byte status);

    /**
     * 查询用户发起的求购请求
     */
    @Select("SELECT * FROM group_purchase_request WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<GroupPurchaseRequest> selectByUserId(@Param("userId") Long userId);

    /**
     * 更新求购请求状态
     */
    @Update("UPDATE group_purchase_request SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Byte status);

    /**
     * 查询过期的求购请求
     */
    @Select("SELECT * FROM group_purchase_request WHERE status = 1 AND expire_time < NOW()")
    List<GroupPurchaseRequest> selectExpiredRequests();
}
