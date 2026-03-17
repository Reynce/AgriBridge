package com.reyn.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reyn.mapper.UserMapper;
import com.reyn.objects.dto.LoginDTO;
import com.reyn.objects.dto.RegisterDTO;
import com.reyn.objects.dto.UserDTO;
import com.reyn.objects.entity.User;
import com.reyn.objects.entity.UserRole;
import com.reyn.service.AuthService;
import com.reyn.service.IUserBackstageService;
import com.reyn.utils.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;
    private final IUserBackstageService iUserBackstageService;

    @Override
    public SaResult login(LoginDTO loginDTO) {
        validateLoginDTO(loginDTO);

        switch (loginDTO.getLoginType()) {
            case PASSWORD:
                return loginByPassword(loginDTO);
            case EMAIL:
                return loginByEmail(loginDTO);
            default:
                throw new IllegalArgumentException("不支持的登录类型: " + loginDTO.getLoginType());
        }
    }

    /**
     * 用户注册
     */
    @Override
    public SaResult register(RegisterDTO dto) {
        asertAccountNotExit(dto.getAccount());
        assertEmailNotExit(dto.getEmail());
        validateEmailCode(dto.getEmail(), dto.getCaptcha());
        confirmPassword(dto.getPassword(), dto.getConfirmPassword());
        Long userId = createUser(dto.getAccount(), dto.getPassword(), dto.getEmail());

        // 添加角色
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(3L);
        return SaResult.ok();
    }

    // ==================== 登录方式 ====================

    /**
     * 通过账号密码登录
     */
    private SaResult loginByPassword(LoginDTO dto) {
        User user = findUserByAccount(dto.getAccount());
        validateCaptcha(dto.getCaptchaKey(), dto.getCaptcha()); // 图形验证码
        validatePassword(dto.getPassword(), user.getPassword());
        saveSession(user);
        return SaResult.ok();
    }

    /**
     * 通过邮箱验证码登录
     */
    private SaResult loginByEmail(LoginDTO dto) {
        User user = findUserByEmail(dto.getAccount());
        validateEmailCode(dto.getAccount(), dto.getCaptcha()); // 邮箱验证码

        // 如果用户不存在，则添加
        if (user == null){
            User u = new User();
            u.setEmail(dto.getAccount());
            iUserBackstageService.insertUser(u);
        }
        saveSession(user);
        return SaResult.ok();
    }

    // ==================== 通用校验方法 ====================

    /**
     * 校验登录 DTO 基础字段非空
     */
    private void validateLoginDTO(LoginDTO dto) {
        if (!StringUtils.hasText(dto.getAccount())) {
            throw new IllegalArgumentException("账号不能为空");
        }
    }

    /**
     * 根据账号（用户名/手机号/邮箱）查询用户（用于密码登录）
     */
    private User findUserByAccount(String account) {
        User user = userMapper.selectOne(
                new QueryWrapper<User>().eq("account", account)
        );
        if (user == null) {
            throw new RuntimeException("账号不存在"); // 统一由全局异常处理器转为 SaResult.error
        }
        return user;
    }

    /**
     * 根据邮箱查询用户（用于邮箱验证码登录）
     */
    private User findUserByEmail(String email) {
        User user = userMapper.selectOne(
                new QueryWrapper<User>().eq("email", email)
        );
        return user;
    }

    /**
     * 校验图形验证码（通用）
     */
    private void validateCaptcha(String captchaKey, String inputCaptcha) {
        if (!StringUtils.hasText(captchaKey) || !StringUtils.hasText(inputCaptcha)) {
            throw new RuntimeException("验证码不能为空");
        }
        String cachedCode = stringRedisTemplate.opsForValue().get(captchaKey);
        if (cachedCode == null || !cachedCode.equalsIgnoreCase(inputCaptcha)) {
            throw new RuntimeException("图形验证码错误或已过期");
        }
        stringRedisTemplate.delete(captchaKey); // 一次性使用
    }

    /**
     * 校验邮箱验证码
     */
    private void validateEmailCode(String email, String inputCode) {
        String key = SystemConstants.EMAIL_CODE_KEY + email;
        String cachedCode = stringRedisTemplate.opsForValue().get(key);
        if (cachedCode == null || !cachedCode.equals(inputCode)) {
            throw new RuntimeException("邮箱验证码错误或已过期");
        }
        stringRedisTemplate.delete(key);
    }

    /**
     * 校验密码（使用 BCrypt）
     */
    private void validatePassword(String rawPassword, String hashedPassword) {
        if (!StringUtils.hasText(rawPassword) || !BCrypt.checkpw(rawPassword, hashedPassword)) {
            throw new RuntimeException("账号或密码错误");
        }
    }

    /**
     * 验证两次密码是否相同
     */
    private void confirmPassword(String pwd, String confirmPwd){
        if (!StringUtils.hasText(pwd) || !pwd.equals(confirmPwd)){
            throw new RuntimeException("密码不一致");
        }
    }

    // ==================== 其他 ====================

    /**
     * 新增用户到数据库
     */
    private Long createUser(String account, String pwd, String email){
        User user = new User();
        user.setAccount(account);
        user.setPassword(BCrypt.hashpw(pwd));
        user.setUsername(UUID.randomUUID().toString());
        user.setEmail(email);

        userMapper.insert(user);

        return user.getId();
    }

    /**
     * 保存用户会话
     */
    private void saveSession(User dbUser) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(dbUser, userDTO);
        StpUtil.login(userDTO.getId());
        StpUtil.getSession().set("user", userDTO);
    }

    /**
     * 确保账号没被使用
     */
    private void asertAccountNotExit(String account){
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("account", account));
        if (user != null){
            throw new RuntimeException("账号已经存在");
        }
    }

    /**
     * 确保邮箱没被使用
     */
    private void assertEmailNotExit(String email) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("email", email));
        if (user != null) {
            throw new RuntimeException("邮箱已被注册");
        }
    }
}