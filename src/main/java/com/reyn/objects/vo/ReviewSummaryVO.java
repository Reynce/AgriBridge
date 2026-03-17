package com.reyn.objects.vo;

import lombok.Data;

@Data
public class ReviewSummaryVO {
    private Double averageRating;
    private Integer totalReviews;
    private Integer ratingCount5;
    private Integer ratingCount4;
    private Integer ratingCount3;
    private Integer ratingCount2;
    private Integer ratingCount1;
    private Integer positiveReviews; // 好评数
    private Integer neutralReviews; // 中评数
    private Integer negativeReviews; // 差评数
}
