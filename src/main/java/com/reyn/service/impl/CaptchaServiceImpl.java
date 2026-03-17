package com.reyn.service.impl;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.RandomUtil;
import com.reyn.objects.vo.CaptchaVO;
import com.reyn.service.CaptchaService;
import com.reyn.service.SysConfigService;
import com.reyn.utils.CaptchaUtil;
import com.reyn.utils.IpUtils;
import com.reyn.utils.Regex.RegexUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.reyn.utils.SystemConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    private final StringRedisTemplate stringRedisTemplate;
    private final JavaMailSender mailSender;
    private final SysConfigService sysConfigService;
    private final HttpServletRequest request;

    private static final String EMAIL_LIMIT_IP_KEY = "email:limit:ip:";
    private static final String EMAIL_LIMIT_EMAIL_KEY = "email:limit:email:";
    private static final String EMAIL_LIMIT_INTERVAL_KEY = "email:limit:interval:";

    @Override
    public SaResult generateCaptcha() throws IOException {
        // 生成验证码文本和图片
        String captchaText = CaptchaUtil.generateText();
        BufferedImage image = CaptchaUtil.createImage(captchaText);

        // 生成唯一键名
        String captchaKey = "captcha:" + UUID.randomUUID().toString();

        // 将验证码存储到Redis中，设置过期时间（例如5分钟）
        stringRedisTemplate.opsForValue().set(captchaKey, captchaText, 5, TimeUnit.MINUTES);

        // 将图片转换为Base64字符串
        String base64Image = imageToBase64(image);

        // 构建返回结果
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaKey(captchaKey);
        captchaVO.setCaptchaImage(base64Image);

        return SaResult.data(captchaVO);
    }

    @Override
    public SaResult sendEmailCode(String email) {
        // 1. 校验邮箱
        if (RegexUtils.isEmailInvalid(email)){
            // 2.不符合返回错误
            log.error("邮箱格式错误: " + email);
            return SaResult.error("邮箱格式错误！");
        }

        // 2. 频率限制检查
        SaResult limitResult = checkEmailRateLimit(email);
        if (limitResult != null) {
            return limitResult;
        }

        // 3. 符合生成验证码
        String code = RandomUtil.randomNumbers(6);
        log.info("为邮箱 " + email + " 生成验证码: " + code);

        // 4.保存验证码到redis，设置有效期
        try {
            stringRedisTemplate.opsForValue().set(EMAIL_CODE_KEY + email, code, EMAIL_CODE_TTL, TimeUnit.MINUTES);
            log.info("验证码已保存到Redis，key: " + EMAIL_CODE_KEY + email);
            
            // 记录发送频率限制（设置间隔时间缓存）
            recordEmailRateLimit(email);
        } catch (Exception e) {
            log.error("保存验证码到Redis失败: " + e.getMessage());
            return SaResult.error("验证码保存失败");
        }

        // 5.异步发送验证码到邮箱
        try {
            log.info("开始异步发送邮件到: " + email);

            // 使用专门的邮件服务异步发送
            sendVerificationCodeAsync(email, code, EMAIL_CODE_TTL);

            log.info("邮件发送任务已提交，立即返回响应");

        } catch (Exception e) {
            log.error("邮件发送任务提交失败: " + e.getMessage(), e);
            return SaResult.error("验证码发送失败，请检查邮箱配置");
        }

        log.info("验证码发送流程完成，邮箱: " + email);
        return SaResult.ok();
    }

    /**
     * 将BufferedImage转换为Base64字符串
     */
    private String imageToBase64(BufferedImage image) throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    //----------------------邮箱发送相关方法---------------------------
    /**
     * 异步发送验证码邮件
     * @param to 收件人邮箱
     * @param code 验证码
     * @param ttl 有效期（分钟）
     * @return CompletableFuture<Boolean> 发送结果
     */
    @Async
    public CompletableFuture<Boolean> sendVerificationCodeAsync(String to, String code, Long ttl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MYEMAIL);
            message.setTo(to);
            message.setSubject("助农生产销售服务平台验证码");
            message.setText("您的验证码为：" + code + "，有效期" + ttl + "分钟，请勿泄露给他人。");

            log.info("开始异步发送验证码邮件到: {}", to);
            mailSender.send(message);
            log.info("验证码邮件发送成功到: {}", to);

            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("异步发送验证码邮件失败: {}", e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * 同步发送验证码邮件（用于测试）
     * @param to 收件人邮箱
     * @param code 验证码
     * @param ttl 有效期（分钟）
     * @return 是否发送成功
     */
    public boolean sendVerificationCodeSync(String to, String code, Long ttl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(MYEMAIL);
            message.setTo(to);
            message.setSubject("Steam商城验证码");
            message.setText("您的验证码为：" + code + "，有效期" + ttl + "分钟，请勿泄露给他人。");

            log.info("开始同步发送验证码邮件到: {}", to);
            mailSender.send(message);
            log.info("验证码邮件发送成功到: {}", to);

            return true;
        } catch (Exception e) {
            log.error("同步发送验证码邮件失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查邮件发送频率限制
     * @param email 目标邮箱
     * @return 限制结果，null表示通过
     */
    private SaResult checkEmailRateLimit(String email) {
        String ip = IpUtils.getIpAddr(request);
        String today = LocalDate.now().toString();

        // 1. 检查发送间隔
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(EMAIL_LIMIT_INTERVAL_KEY + email))) {
            return SaResult.error("发送验证码太频繁，请稍后再试");
        }

        // 2. 检查单IP每日限制
        int ipMax = sysConfigService.selectConfigByKeyInt("mail.limit.ip.max", 10);
        String ipKey = EMAIL_LIMIT_IP_KEY + today + ":" + ip;
        String ipCountStr = stringRedisTemplate.opsForValue().get(ipKey);
        if (ipCountStr != null && Integer.parseInt(ipCountStr) >= ipMax) {
            log.warn("IP: {} 超过今日发送限额: {}", ip, ipMax);
            return SaResult.error("您的IP今日发送验证码次数已达上限");
        }

        // 3. 检查单邮箱每日限制
        int emailMax = sysConfigService.selectConfigByKeyInt("mail.limit.email.max", 5);
        String emailKey = EMAIL_LIMIT_EMAIL_KEY + today + ":" + email;
        String emailCountStr = stringRedisTemplate.opsForValue().get(emailKey);
        if (emailCountStr != null && Integer.parseInt(emailCountStr) >= emailMax) {
            log.warn("邮箱: {} 超过今日发送限额: {}", email, emailMax);
            return SaResult.error("该邮箱今日发送验证码次数已达上限");
        }

        return null;
    }

    /**
     * 记录邮件发送频率
     * @param email 目标邮箱
     */
    private void recordEmailRateLimit(String email) {
        String ip = IpUtils.getIpAddr(request);
        String today = LocalDate.now().toString();

        // 1. 设置发送间隔
        int interval = sysConfigService.selectConfigByKeyInt("mail.limit.interval", 60);
        stringRedisTemplate.opsForValue().set(EMAIL_LIMIT_INTERVAL_KEY + email, "1", interval, TimeUnit.SECONDS);

        // 2. 增加IP当日计数
        String ipKey = EMAIL_LIMIT_IP_KEY + today + ":" + ip;
        stringRedisTemplate.opsForValue().increment(ipKey);
        stringRedisTemplate.expire(ipKey, 24, TimeUnit.HOURS);

        // 3. 增加邮箱当日计数
        String emailKey = EMAIL_LIMIT_EMAIL_KEY + today + ":" + email;
        stringRedisTemplate.opsForValue().increment(emailKey);
        stringRedisTemplate.expire(emailKey, 24, TimeUnit.HOURS);
    }
}
