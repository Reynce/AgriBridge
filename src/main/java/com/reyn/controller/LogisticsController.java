package com.reyn.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.util.SaResult;
import com.reyn.mapper.MerchantMapper;
import com.reyn.mapper.OrderDetailMapper;
import com.reyn.mapper.UserOrderMapper;
import com.reyn.objects.dto.ShipRequestDTO;
import com.reyn.objects.entity.OrderDetail;
import com.reyn.objects.entity.UserOrder;
import com.reyn.objects.vo.LogisticsDisplayVO;
import com.reyn.objects.vo.LogisticsPathVO;
import com.reyn.objects.vo.OrderDetailLogisticsVO;
import com.reyn.service.LogisticsService;
import com.reyn.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/logistics")
@RestController
@RequiredArgsConstructor
public class LogisticsController {
    private final LogisticsService logisticsService;
    private final OrderDetailMapper orderDetailMapper;
    private final UserOrderMapper userOrderMapper;

    /**
     * 获取当前用户商家相关的订单项物流情况
     * @return
     */
    @GetMapping("/list")
    @SaCheckRole("ROLE_PRODUCER")
    public SaResult getOrderDetailLogistics(){
        List<OrderDetailLogisticsVO> logisticsList = logisticsService.getOrderDetailLogisticsBySaller(LoginHelper.getLoginUserId());
        return SaResult.data(logisticsList);
    }

    @PostMapping("/ship")
    @SaCheckRole("ROLE_PRODUCER")
    public SaResult shipOrderItem(@RequestBody ShipRequestDTO request) {

        // 校验订单项是否属于当前商家
        OrderDetail orderDetail = orderDetailMapper.selectById(request.getOrderDetailId());
        if (!LoginHelper.getLoginUserId().equals(orderDetail.getSallerId())) return SaResult.error("订单不属于此商家");

        // 校验订单状态
        UserOrder userOrder = userOrderMapper.selectById(orderDetail.getOrderId());
        if (userOrder.getOrderStatus() != 1 || userOrder.getPaymentStatus() != 1) return SaResult.error("订单状态错误,无法发货");

        logisticsService.shipOrderItem(request);
        return SaResult.ok("发货成功");
    }

    /**
     * 获取物流展示信息（供前端展示使用）
     */
    @GetMapping("/display/{orderDetailId}")
    public SaResult getLogisticsDisplayInfo(@PathVariable Long orderDetailId) {
        // 校验订单项是否存在
        OrderDetail orderDetail = orderDetailMapper.selectById(orderDetailId);
        if (orderDetail == null) return SaResult.error("订单项不存在");

        LogisticsDisplayVO displayInfo = logisticsService.getLogisticsDisplayInfo(orderDetailId);
        return SaResult.data(displayInfo);
    }

}
