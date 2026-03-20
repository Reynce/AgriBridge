package com.reyn.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.crypto.digest.BCrypt;
import com.reyn.objects.dto.LoginDTO;
import com.reyn.objects.dto.RegisterDTO;
import com.reyn.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final com.reyn.service.SysConfigService sysConfigService;

    @PostMapping("/login")
    @SaIgnore
    public SaResult login(@RequestBody LoginDTO loginDTO){
        return authService.login(loginDTO);
    }

    @PostMapping("/register")
    @SaIgnore
    public SaResult register(@RequestBody RegisterDTO registerDTO){
        return authService.register(registerDTO);
    }

    @GetMapping("/logout")
    public SaResult logout(){
        StpUtil.logout();
        return SaResult.ok("退出成功");
    }

    @GetMapping("/captchaEnabled")
    @SaIgnore
    public SaResult getCaptchaEnabled() {
        return SaResult.data(sysConfigService.selectConfigByKeyBoolean("sys.user.captchaEnabled", true));
    }

    // 查询登录状态   ---- http://localhost:8081/acc/isLogin
    @RequestMapping("isLogin")
    public SaResult isLogin(){
        return SaResult.ok("是否登录:" + StpUtil.isLogin());
    }

    // 查询 Token 信息  ---- http://localhost:8081/acc/tokenInfo
    @RequestMapping("tokenInfo")
    public SaResult tokenInfo(){
        return SaResult.data(StpUtil.getTokenInfo());
    }

    // 查询session信息  ---- http://localhost:8081/acc/sessionInfo
    @RequestMapping("sessionInfo")
    public SaResult sessionInfo(){
        return SaResult.data(StpUtil.getSession().get("user"));
    }

}
