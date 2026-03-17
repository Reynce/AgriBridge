package com.reyn.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.vo.CaptchaVO;
import com.reyn.service.CaptchaService;
import com.reyn.utils.CaptchaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 生成图片验证码并返回给前端
     */
    @GetMapping("/img")
    @SaIgnore
    public SaResult generateCaptcha() throws IOException {
        return captchaService.generateCaptcha();
    }

    /**
     * 发送邮箱验证码
     */
    @GetMapping("/sendEmailCode/{email}")
    @SaIgnore
    public SaResult sendEmailCode(@PathVariable String email) {
        return captchaService.sendEmailCode(email);
    }

}
