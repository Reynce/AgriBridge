package com.reyn.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.reyn.objects.dto.UserDTO;

public class LoginHelper {

    public static Long getLoginUserId(){
        if (!StpUtil.isLogin()) return null;
        return ((UserDTO) StpUtil.getSession().get("user")).getId();
    }

    public static String getLoginUserEmail(){
        return ((UserDTO) StpUtil.getSession().get("user")).getEmail();
    }


}
