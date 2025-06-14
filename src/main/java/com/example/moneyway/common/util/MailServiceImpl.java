package com.example.moneyway.common.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine; // ✅ Thymeleaf

    @Override
    public void sendVerificationCodeHtml(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 제목
            helper.setTo(to);
            helper.setSubject("[MoneyWay] 비밀번호 재설정 인증코드");

            // HTML 템플릿 렌더링
            Context context = new Context();
            context.setVariable("code", code);
            String html = templateEngine.process("mail/emailcode", context); // templates/mail/emailcode.html

            // 본문 HTML 설정
            helper.setText(html, true);

            // 전송
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("HTML 메일 전송 실패", e);
        }
    }
}
