package com.reyn.objects.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.reyn.common.core.domain.BaseEntity;
import lombok.Data;


/**
 * 用户信息对象 user
 *
 */
@Data
public class User extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID，主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 登录账号 */
    private String account;

    /** 密码, 建议存储哈希值 */
    private String password;

    /** 邮箱 */
    private String email;

    /** 电话号码 */
    private String phone;

    /** 状态: 激活/未激活/封禁 */
    private Long accountStatus;

    /** 头像地址 */
    private String avatar;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;
}
