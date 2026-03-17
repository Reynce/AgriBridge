package com.reyn.mapper;

import com.reyn.objects.dto.HomeRecommendationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: reyn
 * @date: 2026/3/3 15:55
 * @description: 推荐Mapper
 */
@Mapper
public interface RecommendationMapper {

    /**
     * 获取所有用户的购买记录
     * @return
     */
    List<HomeRecommendationDTO> listAllUserPurchaseHistory();

    /**
     * 获取指定用户购买过的商品ID列表
     * @param userId
     * @return
     */
    List<Long> listPurchasedProductIdsByUserId(@Param("userId") Long userId);

    /**
     * 获取销量最高的商品ID列表
     * @param limit
     * @return
     */
    List<HomeRecommendationDTO> listTopSellingProducts(@Param("limit") int limit);
}
