package com.reyn.service;

import cn.dev33.satoken.util.SaResult;

public interface UserFavoriteService {

    /**
     * 添加收藏
     */
    SaResult addFavorite(Long productId);

    /**
     * 取消收藏
     */
    SaResult cancelFavorite(Long productId);

    /**
     * 获取用户收藏列表
     */
    SaResult getFavoriteList();

    /**
     * 检查是否已收藏
     */
    SaResult checkFavorite(Long productId);

    /**
     * 通过关键词搜索
     */
    SaResult searchByKeyword(String keyword);
}
