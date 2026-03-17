package com.reyn.objects.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductVO {
    private Long id;
    private String title;
    private String description;
    private String categoryName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minStock;
    private List<String> imageUrls;
    private List<SkuVO> skus;
}
