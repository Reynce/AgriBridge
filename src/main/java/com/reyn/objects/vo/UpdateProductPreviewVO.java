package com.reyn.objects.vo;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateProductPreviewVO {
    private Long productId;
    private Long categoryId;
    private String title;
    private String description;
    private String brief;
    private Byte productStatus;
    private List<UpdateProductPreviewVO.UpdateSkuDTO> skus;
    private List<String> imageUrls;

    @Data
    public static class UpdateSkuDTO{
        private Long skuId;
        private String specification;
        private Byte skuStatus;
        private BigDecimal price;
        private Integer stock;
    }
}
