package com.reyn.service;

import com.reyn.objects.dto.HomeRecommendationDTO;

import java.util.List;

/**
 * @author: reyn
 * @date: 2026/3/3 15:48
 * @description: 推荐服务
 */
public interface RecommendationService {

    /**
     * 获取首页推荐
     * @param num
     * @return
     */
    List<HomeRecommendationDTO> getHomeRecommendations(int num);
}
