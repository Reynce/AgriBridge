package com.reyn.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reyn.objects.entity.User;

public interface UserService extends IService<User> {
    SaResult getUserInfo(Long userId);
}
