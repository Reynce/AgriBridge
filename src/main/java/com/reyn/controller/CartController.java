package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.reyn.service.UserCartService;
import com.reyn.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final UserCartService userCartService;

    /**
     * 获取当前用户的购物车列表
     */
    @GetMapping("/list")
    public SaResult getCartList() {
        Long userId = LoginHelper.getLoginUserId();
        return SaResult.data(userCartService.getCartList(userId));
    }

    /**
     * 添加商品到购物车
     * @param skuId 商品规格ID
     * @param quantity 数量
     */
    @PostMapping("/add")
    public SaResult addToCart(@RequestParam Long skuId, @RequestParam Integer quantity) {
        Long userId = LoginHelper.getLoginUserId();
        boolean result = userCartService.addToCart(userId, skuId, quantity);
        return result ? SaResult.ok("添加成功") : SaResult.error("添加失败，可能库存不足");
    }

    /**
     * 更新购物车中商品的数量
     * @param skuId 商品规格ID
     * @param quantity 新数量
     */
    @PutMapping("/update")
    public SaResult updateCart(@RequestParam Long skuId, @RequestParam Integer quantity) {
        Long userId = LoginHelper.getLoginUserId();
        boolean result = userCartService.updateQuantity(userId, skuId, quantity);
        return result ? SaResult.ok("更新成功") : SaResult.error("更新失败，可能库存不足");
    }

    /**
     * 从购物车移除商品
     * @param skuId 商品规格ID
     */
    @DeleteMapping("/removeBySkuId")
    public SaResult removeBySkuId(@RequestParam Long skuId) {
        Long userId = LoginHelper.getLoginUserId();
        boolean result = userCartService.removeFromCart(userId, skuId);
        return result ? SaResult.ok("删除成功") : SaResult.error("删除失败");
    }

    /**
     * 从购物车移除商品
     * @param cartId 购物车项id
     */
    @DeleteMapping("/removeByCartId")
    public SaResult removeByCartId(@RequestParam Long cartId) {
        Long userId = LoginHelper.getLoginUserId();
        boolean result = userCartService.removeByCartId(userId, cartId);
        return result ? SaResult.ok("删除成功") : SaResult.error("删除失败");
    }

    /**
     * 批量从购物车移除商品
     * @param skuIds 商品规格ID列表
     */
    @DeleteMapping("/batch-remove")
    public SaResult batchRemoveFromCart(@RequestBody List<Long> skuIds) {
        Long userId = LoginHelper.getLoginUserId();
        boolean result = userCartService.batchRemoveFromCart(userId, skuIds);
        return result ? SaResult.ok("批量删除成功") : SaResult.error("批量删除失败");
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public SaResult clearCart() {
        Long userId = LoginHelper.getLoginUserId();
        boolean result = userCartService.clearCart(userId);
        return result ? SaResult.ok("清空成功") : SaResult.error("清空失败");
    }
}
