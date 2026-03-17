package com.reyn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.entity.UserCart;
import com.reyn.objects.vo.CartItemVO;

import java.util.List;

public interface UserCartService extends IService<UserCart> {


    /**
     * 添加商品到购物车
     *
     * @param userId 用户ID
     * @param skuId SKU ID
     * @param quantity 数量
     * @return 操作结果
     */
    boolean addToCart(Long userId, Long skuId, Integer quantity);

    /**
     * 更新购物车商品数量
     *
     * @param userId 用户ID
     * @param skuId SKU ID
     * @param quantity 新数量
     * @return 操作结果
     */
    boolean updateQuantity(Long userId, Long skuId, Integer quantity);

    /**
     * 从购物车移除商品
     *
     * @param userId 用户ID
     * @param skuId SKU ID
     * @return 操作结果
     */
    boolean removeFromCart(Long userId, Long skuId);

    boolean removeByCartId(Long userId, Long cartId);

    /**
     * 清空用户购物车
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean clearCart(Long userId);

    /**
     * 批量删除购物车项
     *
     * @param userId 用户ID
     * @param skuIds SKU ID列表
     * @return 操作结果
     */
    boolean batchRemoveFromCart(Long userId, List<Long> skuIds);

    /**
     * 获取用户购物车列表
     *
     * @param userId 用户ID
     * @return 购物车项VO列表
     */
    List<CartItemVO> getCartList(Long userId);
}
