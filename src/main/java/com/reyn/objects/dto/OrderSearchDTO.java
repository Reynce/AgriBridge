package com.reyn.objects.dto;

import lombok.Data;

@Data
public class OrderSearchDTO {
    private String keyword; // 搜索关键词

    private Byte orderStatus;   // 订单状态

    private Byte paymentStatus; // 支付状态

    private Long buyerId;   // 买家id
}
