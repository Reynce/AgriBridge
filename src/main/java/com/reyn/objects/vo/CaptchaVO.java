package com.reyn.objects.vo;

import lombok.Data;

@Data
public class CaptchaVO {
    private String captchaKey;
    private String captchaImage;
    private Boolean captchaEnabled;
}
