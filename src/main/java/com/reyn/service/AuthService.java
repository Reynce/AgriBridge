package com.reyn.service;

import cn.dev33.satoken.util.SaResult;
import com.reyn.objects.dto.LoginDTO;
import com.reyn.objects.dto.RegisterDTO;

public interface AuthService {
    SaResult login(LoginDTO loginDTO);

    SaResult register(RegisterDTO registerDTO);

}
