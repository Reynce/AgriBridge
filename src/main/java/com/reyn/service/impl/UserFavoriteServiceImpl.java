package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.entity.UserFavorite;
import com.reyn.mapper.UserFavoriteMapper;
import com.reyn.objects.vo.FavoriteVO;
import com.reyn.service.UserFavoriteService;
import cn.dev33.satoken.stp.StpUtil;
import com.reyn.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Override
    @Transactional
    public SaResult addFavorite(Long productId) {
        Long userId = StpUtil.getLoginIdAsLong();

        // 检查是否已收藏
        int count = userFavoriteMapper.checkUserFavorite(userId, productId);
        if (count > 0) {
            return SaResult.error("商品已在收藏列表中");
        }

        // 创建收藏记录
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreatedAt(LocalDateTime.now());

        int result = userFavoriteMapper.insert(favorite);
        if (result > 0) {
            return SaResult.ok("收藏成功");
        } else {
            return SaResult.error("收藏失败");
        }
    }

    @Override
    @Transactional
    public SaResult cancelFavorite(Long productId) {
        Long userId = StpUtil.getLoginIdAsLong();

        int result = userFavoriteMapper.deleteUserFavorite(userId, productId);
        if(result <= 0) return SaResult.error("未找到收藏记录");
        return SaResult.ok("取消收藏成功");
    }

    @Override
    public SaResult getFavoriteList() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<FavoriteVO> favoriteList = userFavoriteMapper.getUserFavoriteList(userId);
//        PageInfo<FavoriteVO> pageInfo = new PageInfo<>(favoriteList);
        return SaResult.data(favoriteList);
    }

    @Override
    public SaResult checkFavorite(Long productId) {
        Long userId = StpUtil.getLoginIdAsLong();
        int count = userFavoriteMapper.checkUserFavorite(userId, productId);
        boolean isFavorite = count > 0;
        return SaResult.data(isFavorite);
    }

    @Override
    public SaResult searchByKeyword(String keyword) {
        List<FavoriteVO> favoriteList = userFavoriteMapper.searchByKeyword(LoginHelper.getLoginUserId(), keyword);
        return SaResult.data(favoriteList);
    }
}
