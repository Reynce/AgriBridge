package com.reyn.objects.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ShipRequestDTO {
    @NotNull(message = "订单项id不能为空")
    private Long orderDetailId;
    private String trackingNumber;
    private String logisticsCompany;
}

