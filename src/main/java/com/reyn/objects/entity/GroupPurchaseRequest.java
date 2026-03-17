package com.reyn.objects.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("group_purchase_request")
public class GroupPurchaseRequest {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;        // 发起人用户ID

    private Long orderId;       // 订单关联id

    private String title;       // 商品名称

    private Integer quantity;   // 需求数量（单位：斤）

    private BigDecimal maxTotalPrice; // 最高总价预算（元）

    private String region;      // 收货地区

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime; // 报价截止时间

    private Byte status;        // 状态：1-进行中，2-已成交，3-已过期

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt; // 更新时间
}
