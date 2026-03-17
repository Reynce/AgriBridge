package com.reyn.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reyn.objects.entity.User;
import com.reyn.common.core.controller.BaseController;
import com.reyn.objects.page.TableDataInfo;
import com.reyn.service.IUserBackstageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息Controller
 * 
 * @author ruoyi
 * @date 2026-02-22
 */
@RestController
@RequestMapping("/userBackstage/userBackstage")
public class UserBackstageController extends BaseController
{
    @Autowired
    private IUserBackstageService userService;

    /**
     * 查询用户信息列表
     */
    @GetMapping("/list")
    public TableDataInfo list(User user)
    {
        Page page = startPage();
        Page<User> userPage = userService.selectUserList(page, user);
        return getDataTable(userPage);
    }


    /**
     * 获取用户信息详细信息
     */
    @GetMapping(value = "/{id}")
    public SaResult getInfo(@PathVariable("id") Long id)
    {
        return SaResult.data(userService.selectUserById(id));
    }

    /**
     * 新增用户信息
     */
    @PostMapping
    public SaResult add(@RequestBody User user)
    {
        return SaResult.data(userService.insertUser(user));
    }

    /**
     * 修改用户信息
     */
    @PutMapping
    public SaResult edit(@RequestBody User user)
    {
        return SaResult.data(userService.updateUser(user));
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd/{ids}")
    public SaResult resetPwd(@PathVariable Long[] ids){
        return SaResult.data(userService.resetUserPwdByIds(ids));
    }

    /**
     * 删除用户信息
     */
	@DeleteMapping("/{ids}")
    public SaResult remove(@PathVariable Long[] ids)
    {
        return SaResult.data(userService.deleteUserByIds(ids));
    }

    /**
     * 停用账号
     */
    @PutMapping("/forbid/{ids}")
    public SaResult forbid(@PathVariable Long[] ids){
        return SaResult.data(userService.forbidByIds(ids));
    }

}
