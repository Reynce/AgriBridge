package com.reyn.utils.statusConstants;

/**
 * 订单状态常量
 */
public class OrderStatusConstants {
    // 订单状态
    public static final long ORDER_STATUS_PENDING_SHIP = 1;    // 待发货
    public static final long ORDER_STATUS_SHIPPED = 2;         // 已发货
    public static final long ORDER_STATUS_COMPLETED = 3;       // 已完成
    public static final long ORDER_STATUS_CANCELLED = 4;       // 已取消

    // 支付状态
    public static final long PAYMENT_STATUS_UNPAID = 0;        // 未支付
    public static final long PAYMENT_STATUS_PAID = 1;          // 已支付
}
