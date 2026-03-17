package com.reyn.objects.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户收货地址实体类
 */
@Data
@TableName("address")
public class Address {
    /**
     * 主键，自增，地址记录唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，关联用户表，表示地址归属
     */
    private Long userId;

    /**
     * 收件人姓名
     */
    private String recipientName;

    /**
     * 收件人联系电话
     */
    private String phone;

    /**
     * 省份信息
     */
    private String province;

    /**
     * 城市信息
     */
    private String city;

    /**
     * 区/县信息
     */
    private String district;

    /**
     * 详细地址（街道、门牌号等）
     */
    private String address;

    /**
     * 是否为默认地址，0-否 1-是
     */
    private Integer isDefault;

    /**
     * 地址创建时间
     */
    private Date createdAt;

    /**
     * 地址更新时间
     */
    private Date updatedAt;
}