package com.reyn.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.reyn.objects.entity.UserCart;
import com.reyn.objects.vo.CartItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCartMapper extends BaseMapper<UserCart> {

    /**
     * 根据用户ID获取购物车项列表
     *
     * @param userId 用户ID
     * @return 购物车项VO列表
     */
    List<CartItemVO> getCartItemsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和SKU ID查询购物车项
     *
     * @param userId 用户ID
     * @param skuId SKU ID
     * @return 购物车项
     */
    UserCart getByUserAndSku(@Param("userId") Long userId, @Param("skuId") Long skuId);

    /**
     * 清空指定用户的购物车
     *
     * @param userId 用户ID
     * @return 影响的行数
     */
    int clearCartByUserId(@Param("userId") Long userId);

    /**
     * 删除指定的购物车项
     *
     * @param userId 用户ID
     * @param skuId SKU ID
     * @return 影响的行数
     */
    int deleteByUserAndSku(@Param("userId") Long userId, @Param("skuId") Long skuId);

    /**
     * 根据购物车项ID列表获取购物车项
     *
     * @param cartItemIds 购物车项ID列表
     * @return 购物车项列表
     */
    List<UserCart> getCartItemsByIds(@Param("cartItemIds") List<Long> cartItemIds);
}

