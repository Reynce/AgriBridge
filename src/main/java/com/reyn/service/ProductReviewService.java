package com.reyn.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.ProductReview;
import com.reyn.objects.vo.MerchantReviewVO;

import java.util.List;

public interface ProductReviewService {
    int insertProductReview(ProductReview productReview);

    SaResult getProductReviews(Long productId, Integer page, Integer size);

    SaResult calculateReviewSummary(Long productId);

    Page listReview(Page pageParam, ProductReview productReview);

    /**
     * 获取商家的商品评价
     */
    SaResult getMerchantReviews(Long sellerId, Integer page, Integer size, Boolean recentOnly);

    /**
     * 计算商家的评价总览
     */
    SaResult calculateMerchantReviewSummary(Long sellerId);

    /**
     * 管理员获取所有评价
     */
    SaResult getAdminReviews(Integer page, Integer size, String keyword, Integer rating);

    /**
     * 管理员删除评价
     */
    SaResult deleteReviewByAdmin(Long id);
}
