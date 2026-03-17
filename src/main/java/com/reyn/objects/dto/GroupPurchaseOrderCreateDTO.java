package com.reyn.objects.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GroupPurchaseOrderCreateDTO {
    @NotNull(message = "报价ID不能为空")
    private Long quoteId;

    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    private String remark; // 订单备注
}
