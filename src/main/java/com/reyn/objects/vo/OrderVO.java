package com.reyn.objects.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long buyerId;
    private BigDecimal totalPrice;
    private Long orderStatus;
    private Long paymentStatus;
    private String shippingAddress;
    private List<OrderDetailVO> orderDetails;
    private Date createdAt;
    private Date updatedAt;
}
