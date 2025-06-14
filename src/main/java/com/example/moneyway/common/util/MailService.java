package com.example.moneyway.common.util;

public interface MailService {
    void sendVerificationCodeHtml(String to, String code);
}
