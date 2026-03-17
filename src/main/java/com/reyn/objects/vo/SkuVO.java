package com.reyn.objects.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuVO {
    private Long id;
    private String specification;
    private BigDecimal price;
    private Integer stock;
    private Byte skuStatus;
}
