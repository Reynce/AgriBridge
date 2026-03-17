package com.reyn.objects.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProductReviewVO {
    private Long id;
    private Long userId;
    private String username;
    private String userAvatar;
    private Short rating;
    private String content;
    private List<String> imageUrls;
    private Date createdAt;
}
