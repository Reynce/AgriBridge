package com.reyn.objects.vo;

import lombok.Data;

@Data
public class SellerInfoVO {
    private Long id;
    private String username;
    private String avatar;
    private String phone;
    private String email;
    private Integer productCount; // 商家商品总数
    private Double rating; // 商家评分
    private Integer salesVolume; // 销售量
    private Boolean isCertified; // 是否认证
    private String certificationInfo; // 认证信息
}
