package com.reyn.service.impl;

import com.reyn.mapper.RecommendationMapper;
import com.reyn.objects.dto.HomeRecommendationDTO;
import com.reyn.objects.enums.RecType;
import com.reyn.service.RecommendationService;
import com.reyn.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: reyn
 * @description: 推荐服务实现类
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private RecommendationMapper recommendationMapper;

    @Override
    public List<HomeRecommendationDTO> getHomeRecommendations(int num) {
        Long currentUserId = -1L;
        try {
            currentUserId = LoginHelper.getLoginUserId();
        } catch (Exception e) {
            // 用户未登录，执行冷启动推荐
            return getColdStartRecommendations(num);
        }

        // 1. 获取所有用户的购买历史
        List<HomeRecommendationDTO> allPurchases = recommendationMapper.listAllUserPurchaseHistory();
        Map<Long, Set<Long>> userPurchaseHistory = new HashMap<>();
        for (HomeRecommendationDTO purchase : allPurchases) {
            userPurchaseHistory.computeIfAbsent(purchase.getUserId(), k -> new HashSet<>()).add(purchase.getProductId());
        }

        // 2. 计算用户相似度 (Jaccard Similarity)
        Map<Long, Double> userSimilarity = new HashMap<>();
        Set<Long> currentUserPurchases = userPurchaseHistory.getOrDefault(currentUserId, new HashSet<>());

        // 如果当前用户没有任何购买历史，则直接返回冷启动推荐
        if (currentUserPurchases.isEmpty()) {
            return getColdStartRecommendations(num);
        }

        for (Map.Entry<Long, Set<Long>> entry : userPurchaseHistory.entrySet()) {
            if (entry.getKey().equals(currentUserId)) {
                continue;
            }

            Set<Long> otherUserPurchases = entry.getValue();
            Set<Long> intersection = new HashSet<>(currentUserPurchases);
            intersection.retainAll(otherUserPurchases);

            if (intersection.isEmpty()) {
                continue; // 没有交集，相似度为 0，忽略
            }

            Set<Long> union = new HashSet<>(currentUserPurchases);
            union.addAll(otherUserPurchases);

            double similarity = (double) intersection.size() / union.size();
            userSimilarity.put(entry.getKey(), similarity);
        }

        // 3. 找到最相似的用户
        List<Long> mostSimilarUserIds = userSimilarity.entrySet().stream()
                .filter(e -> e.getValue() > 0) // 再次确保相似度大于 0
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(5) // 取最相似的5个用户
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 如果没有找到相似用户，也返回冷启动推荐
        if (mostSimilarUserIds.isEmpty()) {
            return getColdStartRecommendations(num);
        }

        // 4. 生成推荐
        Set<Long> recommendedProductIds = new HashSet<>();
        for (Long similarUserId : mostSimilarUserIds) {
            Set<Long> similarUserPurchases = new HashSet<>(userPurchaseHistory.get(similarUserId));
            similarUserPurchases.removeAll(currentUserPurchases); // 移除当前用户已购买的
            recommendedProductIds.addAll(similarUserPurchases);
        }

        // 5. 组装推荐结果
        List<HomeRecommendationDTO> recommendations = new ArrayList<>();
        for (Long productId : recommendedProductIds) {
            // 这里从 allPurchases 中查找商品详细信息
            allPurchases.stream()
                    .filter(p -> p.getProductId().equals(productId))
                    .findFirst()
                    .ifPresent(p -> {
                        // 克隆一份，避免修改原始数据
                        HomeRecommendationDTO rec = new HomeRecommendationDTO();
                        org.springframework.beans.BeanUtils.copyProperties(p, rec);
                        rec.setRecType(RecType.PERSONALIZED);
                        rec.setReason("与您相似的用户也购买了");
                        recommendations.add(rec);
                    });
            
            if (recommendations.size() >= num) break;
        }

        // 6.不足则进行补全
        if (recommendations.size() < num) {
            List<HomeRecommendationDTO> coldStartRecommendations = getColdStartRecommendations(num);
            Set<Long> existingProductIds = recommendations.stream()
                    .map(HomeRecommendationDTO::getProductId)
                    .collect(Collectors.toSet());

            for (HomeRecommendationDTO coldStartRecommendation : coldStartRecommendations) {
                if (recommendations.size() < num && !existingProductIds.contains(coldStartRecommendation.getProductId())) {
                    recommendations.add(coldStartRecommendation);
                    existingProductIds.add(coldStartRecommendation.getProductId());
                }
            }
        }

        return recommendations;
    }

    private List<HomeRecommendationDTO> getColdStartRecommendations(int num) {
        List<HomeRecommendationDTO> topSellingProducts = recommendationMapper.listTopSellingProducts(num);
        topSellingProducts.forEach(p -> {
            p.setRecType(RecType.HOT_SALE);
            p.setReason("热销商品");
        });
        return topSellingProducts;
    }
}
