package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.mapper.ProductReviewMapper;
import com.reyn.mapper.UserMapper;
import com.reyn.objects.entity.ProductReview;
import com.reyn.objects.entity.User;
import com.reyn.objects.vo.ProductReviewVO;
import com.reyn.objects.vo.ReviewSummaryVO;
import com.reyn.objects.vo.MerchantReviewVO;
import com.reyn.service.ProductReviewService;
import com.reyn.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {
    @Autowired
    private ProductReviewMapper productReviewMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public SaResult getMerchantReviews(Long sellerId, Integer page, Integer size, Boolean recentOnly) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        Page<MerchantReviewVO> pageParam = new Page<>(page, size);
        LocalDateTime startDate = null;
        if (recentOnly != null && recentOnly) {
            startDate = LocalDateTime.now().minusDays(7); // 最近一周
        }

        Page<MerchantReviewVO> result = productReviewMapper.selectMerchantReviews(pageParam, sellerId, startDate);

        PageResult<MerchantReviewVO> pageResult = new PageResult<>();
        pageResult.setData(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setPages(result.getPages());

        return SaResult.ok("获取评价列表成功").setData(pageResult);
    }

    @Override
    public SaResult getProductReviews(Long productId, Integer page, Integer size) {
        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        // 创建分页对象
        Page<ProductReview> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<ProductReview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductReview::getProductId, productId)
                .orderByDesc(ProductReview::getCreatedAt); // 按创建时间倒序

        // 执行分页查询
        Page<ProductReview> result = productReviewMapper.selectPage(pageParam, queryWrapper);

        // 转换实体为 VO
        List<ProductReviewVO> reviews = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 封装分页结果
        PageResult<ProductReviewVO> pageResult = new PageResult<>();
        pageResult.setData(reviews);
        pageResult.setTotal(result.getTotal());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setPages(result.getPages());

        return SaResult.ok("获取商品评价成功").setData(pageResult);
    }

    // 辅助方法：转换实体为 VO
    private ProductReviewVO convertToVO(ProductReview entity) {
        ProductReviewVO vo = new ProductReviewVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        vo.setRating(entity.getRating());
        vo.setContent(entity.getContent());
        vo.setImageUrls(entity.getReviewImages());
        vo.setCreatedAt(entity.getCreatedAt());

        // 获取用户信息并设置
        User user = userMapper.selectById(entity.getUserId()); // 调用获取用户信息的方法
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setUserAvatar(user.getAvatar());
        }

        return vo;
    }


    @Override
    public SaResult calculateMerchantReviewSummary(Long sellerId) {
        ReviewSummaryVO summary = new ReviewSummaryVO();

        // 获取该商家的所有评价
        List<ProductReview> allReviews = productReviewMapper.selectAllReviewsBySellerId(sellerId);

        if (allReviews.isEmpty()) {
            return SaResult.data(summary);
        }

        // 统计各项指标 (复用逻辑)
        return SaResult.data(buildSummary(allReviews));
    }

    private ReviewSummaryVO buildSummary(List<ProductReview> allReviews) {
        ReviewSummaryVO summary = new ReviewSummaryVO();
        double totalRating = allReviews.stream().mapToDouble(ProductReview::getRating).sum();
        summary.setAverageRating(Math.round(totalRating / allReviews.size() * 10) / 10.0);
        summary.setTotalReviews(allReviews.size());

        long[] ratingCounts = new long[6];
        for (ProductReview review : allReviews) {
            int rating = review.getRating();
            if (rating >= 1 && rating <= 5) {
                ratingCounts[rating]++;
            }
        }

        summary.setRatingCount1((int) ratingCounts[1]);
        summary.setRatingCount2((int) ratingCounts[2]);
        summary.setRatingCount3((int) ratingCounts[3]);
        summary.setRatingCount4((int) ratingCounts[4]);
        summary.setRatingCount5((int) ratingCounts[5]);

        summary.setPositiveReviews((int) allReviews.stream().filter(r -> r.getRating() >= 4).count());
        summary.setNeutralReviews((int) allReviews.stream().filter(r -> r.getRating() == 3).count());
        summary.setNegativeReviews((int) allReviews.stream().filter(r -> r.getRating() <= 2).count());
        return summary;
    }

    @Override
    public SaResult calculateReviewSummary(Long productId) {
        ReviewSummaryVO summary = new ReviewSummaryVO();

        // 获取所有评价
        List<ProductReview> allReviews = productReviewMapper.selectList(
                new LambdaQueryWrapper<ProductReview>().eq(ProductReview::getProductId, productId));

        if (allReviews.isEmpty()) {
            return SaResult.data(summary);
        }

        return SaResult.data(buildSummary(allReviews));
    }

    @Override
    public SaResult getAdminReviews(Integer page, Integer size, String keyword, Integer rating) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        Page<MerchantReviewVO> pageParam = new Page<>(page, size);
        Page<MerchantReviewVO> result = productReviewMapper.selectAdminReviews(pageParam, keyword, rating);

        PageResult<MerchantReviewVO> pageResult = new PageResult<>();
        pageResult.setData(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setPages(result.getPages());

        return SaResult.ok("获取全平台评价成功").setData(pageResult);
    }

    @Override
    public SaResult deleteReviewByAdmin(Long id) {
        int result = productReviewMapper.deleteById(id);
        return result > 0 ? SaResult.ok("删除评价成功") : SaResult.error("删除评价失败");
    }

    @Override
    public int insertProductReview(ProductReview productReview) {
        return productReviewMapper.insert(productReview);
    }

    @Override
    public Page listReview(Page pageParam, ProductReview productReview) {
        QueryWrapper<ProductReview> queryWrapper = new QueryWrapper<>();

        if (productReview.getProductId() != null){
            queryWrapper.eq("productId", productReview.getProductId());
        }

        if (productReview.getOrderId() != null){
            queryWrapper.eq("orderId", productReview.getProductId());
        }

        if (productReview.getUserId() != null){
            queryWrapper.eq("userId", productReview.getProductId());
        }
        if (productReview.getRating() != null){
            queryWrapper.eq("rating", productReview.getRating());
        }
        if (productReview.getContent() != null && !productReview.getContent().isEmpty()){
            queryWrapper.like("conetnt", productReview.getContent());
        }

        Page<ProductReview> page = productReviewMapper.selectPage(pageParam, queryWrapper);

        return page;
    }
}
