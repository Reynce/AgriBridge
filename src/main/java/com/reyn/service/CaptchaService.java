package com.reyn.service;

import cn.dev33.satoken.util.SaResult;

import java.io.IOException;

public interface CaptchaService {
    SaResult generateCaptcha() throws IOException;
    SaResult sendEmailCode(String email);
}
