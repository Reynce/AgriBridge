package com.reyn.service.impl;

import com.reyn.mapper.StatisticsMapper;
import com.reyn.objects.vo.DashboardStatsVO;
import com.reyn.objects.vo.SalesTrendVO;
import com.reyn.objects.vo.UserGrowthVO;
import com.reyn.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计数据Service业务层处理
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    @Override
    public DashboardStatsVO getDashboardStats() {
        return statisticsMapper.getDashboardStats();
    }

    @Override
    public List<SalesTrendVO> getSalesTrend(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        return statisticsMapper.getSalesTrend(startDate, endDate);
    }

    @Override
    public List<UserGrowthVO> getUserGrowthTrend(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        return statisticsMapper.getUserGrowthTrend(startDate, endDate);
    }
}
