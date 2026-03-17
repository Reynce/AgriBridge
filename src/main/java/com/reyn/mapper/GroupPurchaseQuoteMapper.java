package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.GroupPurchaseQuote;
import com.reyn.objects.vo.GroupPurchaseQuoteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupPurchaseQuoteMapper extends BaseMapper<GroupPurchaseQuote> {

    /**
     * 根据求购请求ID查询所有报价
     */
    @Select("SELECT * FROM group_purchase_quote WHERE request_id = #{requestId} ORDER BY quoted_price ASC")
    List<GroupPurchaseQuote> selectByRequestId(@Param("requestId") Long requestId);

    /**
     * 根据商家ID查询报价记录
     */
    @Select("SELECT * FROM group_purchase_quote WHERE seller_id = #{sellerId} ORDER BY created_at DESC")
    List<GroupPurchaseQuote> selectBySellerId(@Param("sellerId") Long sellerId);

    /**
     * 根据商家ID查询报价记录及其关联的求购请求信息
     */
    @Select("SELECT q.*, r.title as requestTitle, r.status as requestStatus " +
            "FROM group_purchase_quote q " +
            "LEFT JOIN group_purchase_request r ON q.request_id = r.id " +
            "WHERE q.seller_id = #{sellerId} " +
            "ORDER BY q.created_at DESC")
    List<GroupPurchaseQuoteVO> selectVOBySellerId(@Param("sellerId") Long sellerId);

    /**
     * 查询某个求购请求的最低报价
     */
    @Select("SELECT * FROM group_purchase_quote WHERE request_id = #{requestId} ORDER BY quoted_price ASC LIMIT 1")
    GroupPurchaseQuote selectLowestQuote(@Param("requestId") Long requestId);
}
