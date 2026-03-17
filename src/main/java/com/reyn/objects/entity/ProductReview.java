package com.reyn.objects.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "product_review", autoResultMap = true)
public class ProductReview {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long orderId;
    private Long orderDetailId;
    private Long productId;
    private String content;
    private Short rating;
    @TableField(value = "review_images",
            typeHandler = com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class)
    private List<String> reviewImages;
    private Date createdAt;
    private Date updatedAt;
}
