package com.reyn.objects.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardStatsVO {
    /**
     * 总销售额
     */
    private BigDecimal totalSales;

    /**
     * 今日新增用户数
     */
    private Long todayNewUsers;

    /**
     * 待处理订单数
     */
    private Long pendingOrders;

    /**
     * 入驻商家数
     */
    private Long activeMerchants;
}
