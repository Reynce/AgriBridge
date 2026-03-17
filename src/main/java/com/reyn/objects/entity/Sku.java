package com.reyn.objects.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("sku")
public class Sku {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private String specification;

    private Byte skuStatus;

    private BigDecimal price;

    private Integer stock;

    @Version
    private Integer version;
}
