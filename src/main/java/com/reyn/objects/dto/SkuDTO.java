package com.reyn.objects.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuDTO {
    private Long id;
    private Long productId;
    private String specification;
    private Byte skuStatus;
    private BigDecimal price;
    private Integer stock;
}
