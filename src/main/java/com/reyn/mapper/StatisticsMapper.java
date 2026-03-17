package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.UserOrder;
import com.reyn.objects.vo.DashboardStatsVO;
import com.reyn.objects.vo.SalesTrendVO;
import com.reyn.objects.vo.UserGrowthVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计数据Mapper接口
 */
public interface StatisticsMapper extends BaseMapper<UserOrder> {

    /**
     * 获取仪表板统计数据
     * @return DashboardStatsVO
     */
    DashboardStatsVO getDashboardStats();

    /**
     * 获取销售趋势数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 销售趋势数据列表
     */
    List<SalesTrendVO> getSalesTrend(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    /**
     * 获取用户增长趋势数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 用户增长趋势数据列表
     */
    List<UserGrowthVO> getUserGrowthTrend(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}
