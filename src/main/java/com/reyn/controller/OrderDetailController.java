package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.reyn.mapper.OrderDetailMapper;
import com.reyn.mapper.UserOrderMapper;
import com.reyn.objects.entity.OrderDetail;
import com.reyn.objects.entity.UserOrder;
import com.reyn.service.OrderDetailService;
import com.reyn.utils.LoginHelper;
import com.reyn.utils.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orderDetail")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailMapper orderDetailMapper;
    private final UserOrderMapper userOrderMapper;
    private final OrderDetailService orderDetailService;

    @PutMapping("/confirmReceive/{orderDetailId}")
    public SaResult confirmReceive(@PathVariable Long orderDetailId){
        // 检查订单项归属
        OrderDetail orderDetail = orderDetailMapper.selectById(orderDetailId);
        UserOrder userOrder = userOrderMapper.selectById(orderDetail.getOrderId());
        if (!LoginHelper.getLoginUserId().equals(userOrder.getBuyerId())) throw new BusinessException("订单项不属于当前用户");

        // 更新订单信息
        orderDetail = new OrderDetail();
        orderDetail.setId(orderDetailId);
        orderDetail.setShippedStatus(2L);
        orderDetailService.updateById(orderDetail);

        return SaResult.ok();
    }
}
