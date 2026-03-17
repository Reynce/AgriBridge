package com.reyn.objects.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_cart")
public class UserCart {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，关联用户表
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 商品规格ID，关联SKU表
     */
    @TableField(value = "sku_id")
    private Long skuId;

    /**
     * 商品数量，默认为1
     */
    @TableField(value = "quantity")
    private Integer quantity;

    /**
     * 添加到购物车的时间
     */
    @TableField(value = "added_at")
    private LocalDateTime addedAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at")
    private LocalDateTime updatedAt;
}

