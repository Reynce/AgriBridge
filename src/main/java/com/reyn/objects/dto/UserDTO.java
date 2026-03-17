package com.reyn.objects.dto;

import com.reyn.objects.vo.RoleVO;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 角色标记集合
     */
    private List<RoleVO> roleVOS;
}
