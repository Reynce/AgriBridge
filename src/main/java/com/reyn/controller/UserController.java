package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.entity.User;
import com.reyn.service.IUserBackstageService;
import com.reyn.service.UserService;
import com.reyn.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
/*
    注解鉴权常用接口:
    @SaCheckLogin: 登录校验 —— 只有登录之后才能进入该方法。
    @SaCheckRole("admin"): 角色校验 —— 必须具有指定角色标识才能进入该方法。
    @SaCheckPermission("user:add"): 权限校验 —— 必须具有指定权限才能进入该方法。
    @SaIgnore：忽略校验 —— 表示被修饰的方法或类无需进行注解鉴权和路由拦截器鉴权。
 */

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final IUserBackstageService userBackstageService;
    @GetMapping("/userInfo")
    public SaResult getUserInfo(){
        return userService.getUserInfo(LoginHelper.getLoginUserId());
    }

    @PutMapping("/resetPwd")
    public SaResult resetPwd(){
        userBackstageService.resetUserPwdByIds(new Long[]{LoginHelper.getLoginUserId()});
        return SaResult.ok();
    }

    @PutMapping
    public SaResult updateUser(@RequestBody User user){
        user.setId(LoginHelper.getLoginUserId());
        user.setAccountStatus(null);
        userBackstageService.updateUser(user);
        return SaResult.ok();
    }
}

