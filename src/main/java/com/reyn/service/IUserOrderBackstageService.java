package com.reyn.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.UserOrder;

/**
 * 订单Service接口
 * 
 * @author ruoyi
 * @date 2026-02-17
 */
public interface IUserOrderBackstageService
{
    /**
     * 查询订单
     * 
     * @param id 订单主键
     * @return 订单
     */
    public UserOrder selectUserOrderById(Long id);

    /**
     * 查询订单列表
     * 
     * @param userOrder 订单
     * @return 订单集合
     */
    public Page<UserOrder> selectUserOrderList(Page page, UserOrder userOrder);

    /**
     * 新增订单
     * 
     * @param userOrder 订单
     * @return 结果
     */
    public int insertUserOrder(UserOrder userOrder);

    /**
     * 修改订单
     * 
     * @param userOrder 订单
     * @return 结果
     */
    public int updateUserOrder(UserOrder userOrder);

    /**
     * 批量删除订单
     * 
     * @param ids 需要删除的订单主键集合
     * @return 结果
     */
    public int deleteUserOrderByIds(Long[] ids);

    /**
     * 删除订单信息
     * 
     * @param id 订单主键
     * @return 结果
     */
    public int deleteUserOrderById(Long id);
}
