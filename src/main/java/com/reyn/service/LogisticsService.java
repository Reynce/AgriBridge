package com.reyn.service;

import com.reyn.objects.dto.ShipRequestDTO;
import com.reyn.objects.vo.LogisticsDisplayVO;
import com.reyn.objects.vo.LogisticsPathVO;

import java.util.List;

public interface LogisticsService {
    List getOrderDetailLogisticsBySaller(Long sallerId);

    void shipOrderItem(ShipRequestDTO request);

    /**
     * 获取物流展示信息（供前端展示使用）
     */
    LogisticsDisplayVO getLogisticsDisplayInfo(Long orderDetailId);
}
