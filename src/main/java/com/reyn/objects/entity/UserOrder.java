package com.reyn.objects.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 订单对象 user_order
 */
@Data
@TableName("user_order")
public class UserOrder
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 买家id */
    private Long buyerId;

    /** 总价 */
    private BigDecimal totalPrice;

    /** 订单状态: 待发货1/已发货2/已完成3/已取消4 */
    private Long orderStatus;

    /** 订单超时时间 */
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date timeoutAt;

    /** 支付状态: 已支付1/未支付0 */
    private Long paymentStatus;

    /** 收货地址,直接存储最终的收货地址,当用户删除自己的地址时不受影响 */
    private String shippingAddress;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    /** 订单详情信息 */
    @TableField(exist = false)
    private List<OrderDetail> orderDetailList;
}
