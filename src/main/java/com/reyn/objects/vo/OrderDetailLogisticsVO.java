package com.reyn.objects.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailLogisticsVO {
    private Long orderDetailId;
    private String productTitle;
    private String skuSpecification;
    private BigDecimal price;
    private Integer quantity;
    private String productMainImage;
    private Byte shippedStatus; // 0未发货，1已发货，2已签收

    private String trackingNumber;
    private String logisticsCompany;

    private String startAt;
    private String currentLocation;
    private String destination;
}

