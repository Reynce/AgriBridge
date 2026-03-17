package com.reyn.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.common.core.controller.BaseController;
import com.reyn.objects.dto.OrderCreateDTO;
import com.reyn.objects.dto.OrderSearchDTO;
import com.reyn.objects.vo.OrderDetailVO;
import com.reyn.service.UserOrderService;
import com.reyn.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@SaCheckLogin
public class OrderController  extends BaseController {

    private final UserOrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public SaResult createOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        return orderService.createOrder(orderCreateDTO);
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/list")
    public SaResult getUserOrders(OrderSearchDTO searchDTO) {
        log.info(searchDTO.getKeyword());
        Page page = startPage();
        searchDTO.setBuyerId(LoginHelper.getLoginUserId());
        return orderService.getUserOrders(page, searchDTO);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public SaResult getOrderDetail(@PathVariable Long orderId) {
        List<OrderDetailVO> orderDetail = orderService.getOrderDetail(orderId);
        if (orderDetail == null) return SaResult.error("查无详情");
        return SaResult.data(orderDetail);
    }

    /**
     * 获取订单详情视图和地址视图
     */
    @GetMapping("/orderDetailAndAddress")
    public SaResult getOrderDetailViewAndAddressView(@RequestParam("orderId") Long orderId){
        return SaResult.data(orderService.getOrderDetailViewAndAddressView(orderId));
    }
}
