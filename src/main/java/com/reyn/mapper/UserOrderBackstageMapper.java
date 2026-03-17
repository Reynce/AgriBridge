package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.OrderDetail;
import com.reyn.objects.entity.UserOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 订单Mapper接口
 *
 * @author ruoyi
 * @date 2026-02-17
 */
@Mapper
public interface UserOrderBackstageMapper extends BaseMapper<UserOrder>
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
    public List<UserOrder> selectUserOrderList(UserOrder userOrder);

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
     * 删除订单
     *
     * @param id 订单主键
     * @return 结果
     */
    public int deleteUserOrderById(Long id);

    /**
     * 批量删除订单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserOrderByIds(Long[] ids);

    /**
     * 批量删除订单详情
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOrderDetailByOrderIds(Long[] ids);

    /**
     * 批量新增订单详情
     *
     * @param orderDetailList 订单详情列表
     * @return 结果
     */
    public int batchOrderDetail(List<OrderDetail> orderDetailList);


    /**
     * 通过订单主键删除订单详情信息
     *
     * @param id 订单ID
     * @return 结果
     */
    public int deleteOrderDetailByOrderId(Long id);
}
