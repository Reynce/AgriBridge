package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.UserOrder;
import com.reyn.common.core.controller.BaseController;
import com.reyn.objects.page.TableDataInfo;
import com.reyn.service.IUserOrderBackstageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 订单Controller
 *
 * @author ruoyi
 * @date 2026-02-17
 */
@RestController
@RequestMapping("/order/order")
public class UserOrderBackstageController extends BaseController
{
    @Autowired
    private IUserOrderBackstageService userOrderService;

    /**
     * 查询订单列表
     */
    @GetMapping("/list")
    public TableDataInfo list(UserOrder userOrder)
    {
        Page page = startPage();
        Page<UserOrder> userOrderPage = userOrderService.selectUserOrderList(page, userOrder);
        return getDataTable(userOrderPage);
    }

    /**
     * 获取订单详细信息
     */
    @GetMapping(value = "/{id}")
    public SaResult getInfo(@PathVariable("id") Long id)
    {
        return SaResult.data(userOrderService.selectUserOrderById(id));
    }

    /**
     * 新增订单
     */
    @PostMapping
    public SaResult add(@RequestBody UserOrder userOrder)
    {
        return SaResult.data(userOrderService.insertUserOrder(userOrder));
    }

    /**
     * 修改订单
     */
    @PutMapping
    public SaResult edit(@RequestBody UserOrder userOrder)
    {
        return SaResult.data(userOrderService.updateUserOrder(userOrder));
    }

    /**
     * 删除订单
     */
	@DeleteMapping("/{ids}")
    public SaResult remove(@PathVariable Long[] ids)
    {
        return SaResult.data(userOrderService.deleteUserOrderByIds(ids));
    }
}
