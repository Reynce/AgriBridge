package com.reyn.objects.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemVO {
    private Long id;                    // 购物车项ID
    private Long skuId;                 // SKU ID
    private String productTitle;         // 商品名称
    private String specification;       // 规格描述
    private BigDecimal price;           // 单价
    private Integer quantity;           // 数量
    private BigDecimal totalPrice;      // 小计价格
    private String imageUrl;            // 商品图片URL
    private Boolean inStock = true;     // 是否有库存
    private Long productId;             // 商品ID
}

