package com.reyn.objects.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantReviewVO extends ProductReviewVO {
    private String productTitle;
    private String productMainImage;
}
