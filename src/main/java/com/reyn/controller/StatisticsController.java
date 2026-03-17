package com.reyn.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.vo.DashboardStatsVO;
import com.reyn.objects.vo.SalesTrendVO;
import com.reyn.objects.vo.UserGrowthVO;
import com.reyn.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据统计Controller
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@SaCheckRole("ROLE_ADMIN")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取仪表板统计数据
     */
    @GetMapping("/dashboard")
    public SaResult getDashboardStats() {
        DashboardStatsVO stats = statisticsService.getDashboardStats();
        return SaResult.data(stats);
    }

    /**
     * 获取销售趋势数据
     */
    @GetMapping("/sales-trend")
    public SaResult getSalesTrend(@RequestParam(defaultValue = "30") int days) {
        List<SalesTrendVO> trendData = statisticsService.getSalesTrend(days);
        return SaResult.data(trendData);
    }

    /**
     * 获取用户增长趋势数据
     */
    @GetMapping("/user-growth")
    public SaResult getUserGrowthTrend(@RequestParam(defaultValue = "30") int days) {
        List<UserGrowthVO> growthData = statisticsService.getUserGrowthTrend(days);
        return SaResult.data(growthData);
    }
}
