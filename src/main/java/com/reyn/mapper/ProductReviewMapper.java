package com.reyn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reyn.objects.entity.ProductReview;
import com.reyn.objects.vo.ProductReviewVO;
import com.reyn.objects.vo.MerchantReviewVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ProductReviewMapper extends BaseMapper<ProductReview> {
    List<ProductReviewVO> selectProductReviews(@Param("productId") Long productId,
                                               @Param("offset") int offset,
                                               @Param("size") int size);

    /**
     * 根据商家ID查询评价（联表查询产品信息）
     */
    Page<MerchantReviewVO> selectMerchantReviews(Page<MerchantReviewVO> page, @Param("sellerId") Long sellerId, @Param("startDate") LocalDateTime startDate);

    /**
     * 根据商家ID查询所有评价
     */
    @Select("SELECT pr.* FROM product_review pr LEFT JOIN product p ON pr.product_id = p.id WHERE p.saller_id = #{sellerId}")
    List<ProductReview> selectAllReviewsBySellerId(@Param("sellerId") Long sellerId);

    /**
     * 管理员查询所有评价（支持过滤）
     */
    Page<MerchantReviewVO> selectAdminReviews(Page<MerchantReviewVO> page, @Param("keyword") String keyword, @Param("rating") Integer rating);
}
