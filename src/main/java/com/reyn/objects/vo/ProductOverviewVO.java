// ProductOverviewVO.java
package com.reyn.objects.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductOverviewVO {
    private Long id;
    private String title;
    private String brief; // 简要描述
    private String categoryName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String mainImageUrl; // 主图URL
}
