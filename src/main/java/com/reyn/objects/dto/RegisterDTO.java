package com.reyn.objects.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String account;
    private String password;
    private String confirmPassword;
    private String email;
    private String captcha;
}
