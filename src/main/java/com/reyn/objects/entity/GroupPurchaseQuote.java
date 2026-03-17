package com.reyn.objects.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("group_purchase_quote")
public class GroupPurchaseQuote {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long requestId;     // 关联的求购请求ID

    private Long sellerId;      // 报价商家用户ID

    private BigDecimal quotedPrice; // 报价单价（元/斤）

    private String deliveryDesc; // 发货说明

    private String productImage;    //货品图片

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 报价创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt; // 更新时间
}
