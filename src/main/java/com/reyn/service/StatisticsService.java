package com.reyn.service;

import com.reyn.objects.vo.DashboardStatsVO;
import com.reyn.objects.vo.SalesTrendVO;
import com.reyn.objects.vo.UserGrowthVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计数据Service接口
 */
public interface StatisticsService {

    /**
     * 获取仪表板统计数据
     * @return DashboardStatsVO
     */
    DashboardStatsVO getDashboardStats();

    /**
     * 获取销售趋势数据
     * @param days 天数范围（最近多少天）
     * @return 销售趋势数据列表
     */
    List<SalesTrendVO> getSalesTrend(int days);

    /**
     * 获取用户增长趋势数据
     * @param days 天数范围（最近多少天）
     * @return 用户增长趋势数据列表
     */
    List<UserGrowthVO> getUserGrowthTrend(int days);
}
