package com.reyn.objects.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GroupPurchaseQuoteDTO {

    @NotNull(message = "求购请求ID不能为空")
    private Long requestId;

    @NotNull(message = "报价单价不能为空")
    private BigDecimal quotedPrice;

    private String deliveryDesc;
}
