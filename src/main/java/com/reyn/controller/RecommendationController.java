package com.reyn.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.util.SaResult;
import com.reyn.common.core.controller.BaseController;
import com.reyn.objects.dto.HomeRecommendationDTO;
import com.reyn.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: reyn
 * @date: 2026/3/3 15:45
 * @description: 推荐接口
 */
@RestController
@RequestMapping("/recommendation")
public class RecommendationController extends BaseController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * 获取首页推荐
     * @return
     */
    @GetMapping("/home")
    @SaIgnore
    public SaResult getHomeRecommendations(@RequestParam(defaultValue = "5") int num) {

        return SaResult.data(recommendationService.getHomeRecommendations(num));
    }
}
