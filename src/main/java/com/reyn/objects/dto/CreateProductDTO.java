package com.reyn.objects.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.*;

@Data
public class CreateProductDTO {
    private Long productId;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    private String brief;

    @Min(value = 0, message = "产品状态必须是非负数")
    @Max(value = 2, message = "产品状态只能是0、1或2")
    private Byte productStatus;

    @NotEmpty(message = "SKU列表不能为空")
    private List<CreateSkuDTO> skus;

    @NotEmpty(message = "图片列表不能为空")
    @Size(message = "最多只允许5张图片")
    private List<String> imageUrls;

    @Data
    public static class CreateSkuDTO{
        private Long skuId;

        @NotBlank(message = "规格描述不能为空")
        private String specification;
        private Byte skuStatus;

        @Digits(integer = 10, fraction = 2, message = "小数位最多允许两位")
        @DecimalMin(value = "0.00", message = "价格不能为负数")
        private BigDecimal price;

        @Min(value = 0, message = "库存不能为负数")
        private Integer stock;
    }
}

