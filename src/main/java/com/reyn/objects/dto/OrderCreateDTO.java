package com.reyn.objects.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderCreateDTO {
    @NotEmpty(message = "订单项不能为空")
    private List<OrderItemDTO> orderItems;

    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;
}
