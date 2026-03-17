package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.LogisticsInfo;
import com.reyn.objects.vo.OrderDetailLogisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LogisticsInfoMapper extends BaseMapper<LogisticsInfo> {

    @Select({
            "<script>",
            "SELECT ",
            "od.id AS orderDetailId, ",
            "od.product_title AS productTitle, ",
            "od.sku_specification AS skuSpecification, ",
            "od.price, ",
            "od.quantity, ",
            "od.product_main_image AS productMainImage, ",
            "od.shipped_status AS shippedStatus, ",
            "li.tracking_number AS trackingNumber, ",
            "li.logistics_company AS logisticsCompany, ",
            "li.start_from AS startAt, ",
            "li.current_location AS currentLocation, ",
            "uo.shipping_address AS destination ",
            "FROM order_detail od ",
            "LEFT JOIN logistics_info li ON od.id = li.order_detail_id ",
            "JOIN user_order uo ON od.order_id = uo.id ",
            "WHERE od.saller_id = #{sallerId}",
            "</script>"
    })
    List<OrderDetailLogisticsVO> selectLogisticsByMerchant(@Param("sallerId") Long sallerId);

    /**
     * 根据订单详情ID获取物流信息
     */
    @Select("SELECT * FROM logistics_info WHERE order_detail_id = #{orderDetailId}")
    LogisticsInfo selectByOrderDetailId(@Param("orderDetailId") Long orderDetailId);
}
