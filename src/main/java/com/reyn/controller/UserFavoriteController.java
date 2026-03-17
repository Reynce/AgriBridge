package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.reyn.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/{productId}")
    public SaResult addFavorite(@PathVariable Long productId) {
        return userFavoriteService.addFavorite(productId);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{productId}")
    public SaResult cancelFavorite(@PathVariable Long productId) {
        return userFavoriteService.cancelFavorite(productId);
    }

    /**
     * 获取收藏列表
     */
    @GetMapping
    public SaResult getFavoriteList() {
        return userFavoriteService.getFavoriteList();
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{productId}")
    public SaResult checkFavorite(@PathVariable Long productId) {
        return userFavoriteService.checkFavorite(productId);
    }

    /**
     * 根据关键词筛选
     */
    @GetMapping("/search")
    public SaResult searchByKeyword(@RequestParam("keyword") String keyword) {
        if(!StringUtils.hasText(keyword)) return SaResult.error("关键词不能为空");
        return userFavoriteService.searchByKeyword(keyword);
    }
}
