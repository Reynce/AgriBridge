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
@TableName("merchant")
public class Merchant {
    @TableId(type = IdType.AUTO)
    private Long id;           // 商家ID

    private Long userId;       // 店主用户ID

    private String name;       // 店铺名称

    private String logo;       // 店铺logo

    private String description; // 店铺简介

    private Byte status;       // 状态: 审核中(1)/已上线(2)/已封禁(0)

    private String licenseImage; // 营业执照图片

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<Long> categoryIds;   // 经营类目ID列表，如 [1, 5, 12]

    private Date createdAt; // 创建时间

    private Date updatedAt; // 更新时间
}
