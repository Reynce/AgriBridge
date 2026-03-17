package com.reyn.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.User;

/**
 * 用户信息Service接口
 * 
 * @author ruoyi
 * @date 2026-02-22
 */
public interface IUserBackstageService
{
    /**
     * 查询用户信息
     * 
     * @param id 用户信息主键
     * @return 用户信息
     */
    public User selectUserById(Long id);

    /**
     * 查询用户信息列表
     *
     * @param user 用户信息
     * @return 用户信息集合
     */
    public Page<User> selectUserList(Page page, User user);

    /**
     * 新增用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(User user);

    /**
     * 重置用户密码
     */
    public int resetUserPwdByIds(Long[] ids);

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(User user);

    /**
     * 批量删除用户信息
     * 
     * @param ids 需要删除的用户信息主键集合
     * @return 结果
     */
    public int deleteUserByIds(Long[] ids);

    /**
     * 删除用户信息信息
     * 
     * @param id 用户信息主键
     * @return 结果
     */
    public int deleteUserById(Long id);

    public int forbidByIds(Long[] ids);
}
