package com.reyn.objects.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GroupPurchaseRequestDTO {

    @NotBlank(message = "商品名称不能为空")
    private String title;

    @NotNull(message = "需求数量不能为空")
    private Integer quantity;

    @NotNull(message = "最高总价预算不能为空")
    private BigDecimal maxTotalPrice;

    private String region;

    @NotNull(message = "报价截止时间不能为空")
    private LocalDateTime expireTime;
}
