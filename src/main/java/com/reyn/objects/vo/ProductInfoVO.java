package com.reyn.objects.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductInfoVO {
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
    private Boolean hasFavorited = Boolean.FALSE;   // 是否收藏
    private Long sallerId;
}