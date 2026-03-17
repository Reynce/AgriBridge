package com.reyn.objects.entity;

import java.sql.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;

    private Long sallerId;

    private Long categoryId;

    private String title;

    private String description;

    private String brief;

    private Byte productStatus;

    private Date createdAt;

    private Date updatedAt;
}
