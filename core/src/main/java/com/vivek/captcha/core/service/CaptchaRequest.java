package com.vivek.captcha.core.service;

import lombok.Builder;
import lombok.Getter;

@Builder
public class CaptchaRequest {
    @Getter protected String remoteIpAddress;
    @Getter protected String captchaResponse;
}
