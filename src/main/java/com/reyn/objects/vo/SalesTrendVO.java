package com.reyn.objects.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesTrendVO {
    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 当日销售额
     */
    private BigDecimal salesAmount;

    /**
     * 订单数量
     */
    private Long orderCount;
}
