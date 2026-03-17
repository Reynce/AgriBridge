package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.UserFavorite;
import com.reyn.objects.vo.FavoriteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    /**
     * 获取用户的收藏列表
     */
    List<FavoriteVO> getUserFavoriteList(@Param("userId") Long userId);

    /**
     * 检查用户是否已收藏某商品
     */
    int checkUserFavorite(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 删除用户收藏
     */
    int deleteUserFavorite(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 根据关键词搜索用户收藏
     */
    List<FavoriteVO> searchByKeyword(@Param("userId") Long userId,@Param("keyword") String keyword);
}
