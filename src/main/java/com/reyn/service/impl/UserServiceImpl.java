package com.reyn.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reyn.mapper.UserMapper;
import com.reyn.mapper.UserRoleMapper;
import com.reyn.objects.dto.UserDTO;
import com.reyn.objects.entity.User;
import com.reyn.service.UserService;
import com.reyn.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserRoleMapper userRoleMapper;
    @Override
    public SaResult getUserInfo(Long userId) {
        UserDTO userDTO = (UserDTO) StpUtil.getSession().get("user");
        userDTO.setRoleVOS(userRoleMapper.selectRoleKeysAndNamesByUserId(userId));

        return SaResult.data(userDTO);
    }
}
