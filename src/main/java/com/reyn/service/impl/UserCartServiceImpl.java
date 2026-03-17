package com.reyn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.objects.entity.*;
import com.reyn.mapper.*;
import com.reyn.objects.vo.CartItemVO;
import com.reyn.service.UserCartService;
import com.reyn.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCartServiceImpl extends ServiceImpl<UserCartMapper, UserCart> implements UserCartService {

    private final UserCartMapper userCartMapper;
    private final SkuMapper skuMapper;
    private final AddressMapper addressMapper;
    private final UserOrderMapper userOrderMapper;
    private final OrderDetailMapper orderDetailMapper;

    @Override
    public List<CartItemVO> getCartList(Long userId) {
        return userCartMapper.getCartItemsByUserId(userId);
    }

    @Override
    @Transactional
    public boolean addToCart(Long userId, Long skuId, Integer quantity) {
        // 验证SKU是否存在且有库存
        Sku sku = skuMapper.selectById(skuId);
        if (sku == null || sku.getStock() <= 0) {
            return false;
        }

        // 检查是否已经存在于购物车
        UserCart existingCart = userCartMapper.getByUserAndSku(userId, skuId);
        if (existingCart != null) {
            // 如果存在，则更新数量
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            existingCart.setUpdatedAt(LocalDateTime.now());
            return this.updateById(existingCart);
        } else {
            // 如果不存在，则新增
            UserCart newCart = new UserCart();
            newCart.setUserId(userId);
            newCart.setSkuId(skuId);
            newCart.setQuantity(quantity);
            newCart.setAddedAt(LocalDateTime.now());
            newCart.setUpdatedAt(LocalDateTime.now());
            return this.save(newCart);
        }
    }

    @Override
    @Transactional
    public boolean updateQuantity(Long userId, Long skuId, Integer quantity) {
        if (quantity <= 0) {
            return removeByUserAndSku(userId, skuId);
        }

        // 验证库存
        Sku sku = skuMapper.selectById(skuId);
        if (sku == null || sku.getStock() < quantity) {
            return false;
        }

        UserCart cart = userCartMapper.getByUserAndSku(userId, skuId);
        if (cart != null) {
            cart.setQuantity(quantity);
            cart.setUpdatedAt(LocalDateTime.now());
            return this.updateById(cart);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean removeFromCart(Long userId, Long skuId) {
        return removeByUserAndSku(userId, skuId);
    }

    @Override
    @Transactional
    public boolean removeByCartId(Long userId, Long cartId) {
        return removeByUserAndCart(userId, cartId);
    }

    @Override
    @Transactional
    public boolean clearCart(Long userId) {
        return userCartMapper.clearCartByUserId(userId) > 0;
    }

    @Override
    @Transactional
    public boolean batchRemoveFromCart(Long userId, List<Long> skuIds) {
        for (Long skuId : skuIds) {
            removeByUserAndSku(userId, skuId);
        }
        return true;
    }

    /**
     * 构建收货地址字符串
     */
    private String buildShippingAddressString(Address address) {
        return address.getProvince() + address.getCity() + address.getDistrict() +
                address.getAddress() + " " + address.getRecipientName() + " " + address.getPhone();
    }

    /**
     * 私有方法：根据用户ID和SKU ID删除购物车项
     */
    private boolean removeByUserAndSku(Long userId, Long skuId) {
        LambdaQueryWrapper<UserCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCart::getUserId, userId).eq(UserCart::getSkuId, skuId);
        return this.remove(wrapper);
    }

    /**
     * 私有方法：根据用户ID和Cart ID删除购物车项
     */
    private boolean removeByUserAndCart(Long userId, Long cartId) {
        LambdaQueryWrapper<UserCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCart::getUserId, userId).eq(UserCart::getId, cartId);
        return this.remove(wrapper);
    }
}
