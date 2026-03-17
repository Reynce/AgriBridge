package com.reyn.objects.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantProductsVO {
    private Long id;
    private String title;
    private String image;
    private String category;
    private Byte product_status;
    private Long stock;
    private BigDecimal price;
}
