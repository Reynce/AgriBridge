package com.reyn.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.OrderDetail;
import com.reyn.objects.entity.UserOrder;
import com.reyn.mapper.UserOrderBackstageMapper;
import com.reyn.service.IUserOrderBackstageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;


/**
 * 订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-17
 */
@Service
public class UserOrderBackstageServiceImpl implements IUserOrderBackstageService
{
    @Autowired
    private UserOrderBackstageMapper userOrderMapper;

    /**
     * 查询订单
     * 
     * @param id 订单主键
     * @return 订单
     */
    @Override
    public UserOrder selectUserOrderById(Long id)
    {
        return userOrderMapper.selectUserOrderById(id);
    }

    /**
     * 查询订单列表
     * 
     * @param userOrder 订单
     * @return 订单
     */
    @Override
    public Page<UserOrder> selectUserOrderList(Page page, UserOrder userOrder) {
        QueryWrapper<UserOrder> queryWrapper = new QueryWrapper<>();

        // 封装 orderNo 查询条件
        if (userOrder.getOrderNo() != null && !userOrder.getOrderNo().isEmpty()) {
            queryWrapper.like("order_no", userOrder.getOrderNo());
        }

        // 封装 buyerId 查询条件
        if (userOrder.getBuyerId() != null) {
            queryWrapper.eq("buyer_id", userOrder.getBuyerId());
        }

        // 封装 orderStatus 查询条件
        if (userOrder.getOrderStatus() != null) {
            queryWrapper.eq("order_status", userOrder.getOrderStatus());
        }

        // 封装 paymentStatus 查询条件
        if (userOrder.getPaymentStatus() != null) {
            queryWrapper.eq("payment_status", userOrder.getPaymentStatus());
        }

        // 封装 shippingAddress 查询条件
        if (userOrder.getShippingAddress() != null && !userOrder.getShippingAddress().isEmpty()) {
            queryWrapper.like("shipping_address", userOrder.getShippingAddress());
        }

        // 执行分页查询
        return userOrderMapper.selectPage(page, queryWrapper);
    }

    /**
     * 新增订单
     * 
     * @param userOrder 订单
     * @return 结果
     */
    @Transactional
    @Override
    public int insertUserOrder(UserOrder userOrder)
    {
        int rows = userOrderMapper.insertUserOrder(userOrder);
        insertOrderDetail(userOrder);
        return rows;
    }

    /**
     * 修改订单
     * 
     * @param userOrder 订单
     * @return 结果
     */
    @Transactional
    @Override
    public int updateUserOrder(UserOrder userOrder)
    {
        userOrderMapper.deleteOrderDetailByOrderId(userOrder.getId());
        insertOrderDetail(userOrder);
        return userOrderMapper.updateUserOrder(userOrder);
    }

    /**
     * 批量删除订单
     * 
     * @param ids 需要删除的订单主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteUserOrderByIds(Long[] ids)
    {
        userOrderMapper.deleteOrderDetailByOrderIds(ids);
        return userOrderMapper.deleteUserOrderByIds(ids);
    }

    /**
     * 删除订单信息
     * 
     * @param id 订单主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteUserOrderById(Long id)
    {
        userOrderMapper.deleteOrderDetailByOrderId(id);
        return userOrderMapper.deleteUserOrderById(id);
    }

    /**
     * 新增订单详情信息
     * 
     * @param userOrder 订单对象
     */
    public void insertOrderDetail(UserOrder userOrder)
    {
        List<OrderDetail> orderDetailList = userOrder.getOrderDetailList();
        Long id = userOrder.getId();
        if (orderDetailList != null && orderDetailList.size() != 0)
        {
            List<OrderDetail> list = new ArrayList<OrderDetail>();
            for (OrderDetail orderDetail : orderDetailList)
            {
                orderDetail.setOrderId(id);
                list.add(orderDetail);
            }
            if (list.size() > 0)
            {
                userOrderMapper.batchOrderDetail(list);
            }
        }
    }
}
