package com.vivek.captcha.core.service;

public interface CaptchaService {
    boolean isValidCaptcha(final CaptchaRequest request);
}
