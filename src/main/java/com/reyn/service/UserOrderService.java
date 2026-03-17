package com.reyn.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.dto.OrderCreateDTO;
import com.reyn.objects.dto.OrderSearchDTO;
import com.reyn.objects.entity.UserOrder;
import com.reyn.objects.vo.OrderDetailVO;
import com.reyn.objects.vo.PayOrderVO;

import java.util.List;

public interface UserOrderService extends IService<UserOrder> {
    /**
     * 创建订单
     */
    SaResult createOrder(OrderCreateDTO orderCreateDTO);

    /**
     * 获取用户订单列表
     */
    SaResult getUserOrders(Page pageParam, OrderSearchDTO searchDTO);

    /**
     * 获取订单详情
     */
    List<OrderDetailVO> getOrderDetail(Long orderId);

    PayOrderVO getOrderDetailViewAndAddressView(Long orderId);
}
