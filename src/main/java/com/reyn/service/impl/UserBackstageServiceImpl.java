package com.reyn.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.User;
import com.reyn.mapper.UserBackstageMapper;
import com.reyn.mapper.UserRoleMapper;
import com.reyn.objects.entity.UserRole;
import com.reyn.service.IUserBackstageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * 用户信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-22
 */
@Service
public class UserBackstageServiceImpl implements IUserBackstageService
{
    @Autowired
    private UserBackstageMapper userMapper;

    @Value("${system-diy-cfg.default-pwd}")
    private String defaultPwd;
    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 查询用户信息
     * 
     * @param id 用户信息主键
     * @return 用户信息
     */
    @Override
    public User selectUserById(Long id)
    {
        return userMapper.selectUserById(id);
    }

    /**
     * 查询用户信息列表
     *
     * @param user 用户信息
     * @return 用户信息
     */
    public Page<User> selectUserList(Page page, User user)
    {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            queryWrapper.like(User::getUsername, user.getUsername().trim());
        }
        if (user.getAccount() != null && !user.getAccount().trim().isEmpty()) {
            queryWrapper.like(User::getAccount, user.getAccount().trim());
        }
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            queryWrapper.like(User::getEmail, user.getEmail().trim());
        }
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            queryWrapper.like(User::getPhone, user.getPhone().trim());
        }
        if (user.getAccountStatus() != null) {
            queryWrapper.eq(User::getAccountStatus, user.getAccountStatus());
        }

        // 执行分页查询
        return userMapper.selectPage(page, queryWrapper);
        }

    /**
     * 新增用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int insertUser(User user)
    {
        /// 校验用户账号和邮箱是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();

        // 校验账号是否已存在
        if (user.getAccount() != null && !user.getAccount().isEmpty()) {
            queryWrapper.clear();
            queryWrapper.eq("account", user.getAccount());
            User existingAccount = userMapper.selectOne(queryWrapper);
            if (existingAccount != null) {
                throw new RuntimeException("账号已存在");
            }
        }

        // 校验邮箱是否已存在
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            queryWrapper.clear();
            queryWrapper.eq("email", user.getEmail());
            User existingEmail = userMapper.selectOne(queryWrapper);
            if (existingEmail != null) {
                throw new RuntimeException("邮箱已被注册");
            }
        }

        // 校验手机号是否已存在
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            queryWrapper.clear();
            queryWrapper.eq("phone", user.getPhone());
            User existingPhone = userMapper.selectOne(queryWrapper);
            if (existingPhone != null) {
                throw new RuntimeException("手机号已被注册");
            }
        }

        // 若密码为空设置默认密码
        if (user.getPassword().isEmpty()){
            user.setPassword(defaultPwd);
        }
        // 密码加密
        user.setPassword(BCrypt.hashpw(user.getPassword()));

        // 用户新增结果
        int res = userMapper.insertUser(user);
        if (res > 0){
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(3L);
            userRoleMapper.insert(userRole);
        }

        return res;
    }

    /**
     * 重置用户密码
     * @param ids
     * @return
     */
    @Override
    public int resetUserPwdByIds(Long[] ids){
        if (ids == null || ids.length == 0) return 0;
        return userMapper.resetUserPwd(ids, BCrypt.hashpw(defaultPwd));
    }

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUser(User user)
    {
        // 密码加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()){
            user.setPassword(BCrypt.hashpw(user.getPassword()));
        }
        return userMapper.updateUser(user);
    }

    /**
     * 批量删除用户信息
     * 
     * @param ids 需要删除的用户信息主键
     * @return 结果
     */
    @Override
    public int deleteUserByIds(Long[] ids)
    {
        return userMapper.deleteUserByIds(ids);
    }

    /**
     * 删除用户信息信息
     * 
     * @param id 用户信息主键
     * @return 结果
     */
    @Override
    public int deleteUserById(Long id)
    {
        return userMapper.deleteUserById(id);
    }

    @Override
    public int forbidByIds(Long[] ids){
        //非空数组校验
        if (ids == null || ids.length == 0) return 0;
        return userMapper.forbidByIds(ids);
    }
}
