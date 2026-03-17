package com.reyn.config.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.reyn.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限加载接口实现
 *  "*" 代表任何权限
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserRoleMapper userRoleMapper;

    /**
     * 返回一个账号拥有的权限标识集合
     * @param loginId 调用StpUtil.login()时填写的登录id
     * @param loginType 登录类型
     * @return 一个账号拥有的权限标识集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 模拟权限标识集合的获取
        List<String> permissionList = new ArrayList<>();
        // TODO 获取权限集合
        return permissionList;
    }

    /**
     * 返回一个账号拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return userRoleMapper.selectRoleKeysByUserId(Long.valueOf((String) loginId));
    }

    private void checkPermission(){
        // 获取：当前账号所拥有的权限集合
        StpUtil.getPermissionList();

        // 判断：当前账号是否含有指定权限, 返回 true 或 false
        StpUtil.hasPermission("user.add");

        // 校验：当前账号是否含有指定权限, 如果验证未通过，则抛出异常: NotPermissionException
        StpUtil.checkPermission("user.add");

        // 校验：当前账号是否含有指定权限 [指定多个，必须全部验证通过]
        StpUtil.checkPermissionAnd("user.add", "user.delete", "user.get");

        // 校验：当前账号是否含有指定权限 [指定多个，只要其一验证通过即可]
        StpUtil.checkPermissionOr("user.add", "user.delete", "user.get");

    }

    private void checkRole(){
        // 获取：当前账号所拥有的角色集合
        StpUtil.getRoleList();

        // 判断：当前账号是否拥有指定角色, 返回 true 或 false
        StpUtil.hasRole("super-admin");

        // 校验：当前账号是否含有指定角色标识, 如果验证未通过，则抛出异常: NotRoleException
        StpUtil.checkRole("super-admin");

        // 校验：当前账号是否含有指定角色标识 [指定多个，必须全部验证通过]
        StpUtil.checkRoleAnd("super-admin", "shop-admin");

        // 校验：当前账号是否含有指定角色标识 [指定多个，只要其一验证通过即可]
        StpUtil.checkRoleOr("super-admin", "shop-admin");

    }
}
