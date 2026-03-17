package com.reyn.objects.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String account;        // 账号/邮箱/手机号（通用字段）
    private String password;       // 密码（仅账号密码登录使用）
    private String captcha;        // 验证码
    private String captchaKey;     // 验证码唯一标识（用于 Redis 校验）
    private LoginType loginType;   // 登录类型
}

