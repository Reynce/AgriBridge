package com.reyn.objects.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailVO {
    private Long id;
    private Long orderId;
    private String productTitle;
    private Long skuId;
    private String skuSpecification;
    private Integer quantity;
    private BigDecimal price;
    private String imgUrl;
    private Long shippedStatus;
    private Byte isReview;
}
