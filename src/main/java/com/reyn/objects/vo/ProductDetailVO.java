// ProductDetailVO.java
package com.reyn.objects.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailVO {
    private Long id;
    private String title;
    private String brief; // 商品简介
    private String description; // 完整的富文本详情
    private String categoryName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private List<String> imageUrls; // 所有图片URL
    private String mainImageUrl; // 主图URL
    private List<SkuVO> skus; // SKU列表
    private Long merchantId;

    // 商家信息
    private SellerInfoVO sellerInfo;

    // 商品溯源信息
    private TraceabilityInfoVO traceabilityInfo;

    // 库存信息
    private Integer totalStock;

    // 评价信息
    private ReviewSummaryVO reviewSummary;

    // 收藏状态
    private Boolean isFavorite;
}
