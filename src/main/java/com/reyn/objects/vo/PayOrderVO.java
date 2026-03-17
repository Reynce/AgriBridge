package com.reyn.objects.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PayOrderVO {
    private Long orderId;
    private String orderNo;
    private List<OrderDetailVO> orderDetailVOList;
    private AddressVO addressVO;
    private BigDecimal totalPrice;
    private Date createdAt;
}
