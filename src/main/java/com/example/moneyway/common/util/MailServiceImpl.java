package com.example.moneyway.common.util;

import com.example.moneyway.common.exception.CustomException.CustomUserException; // 예시로 UserException 사용
import com.example.moneyway.common.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async; // [개선]
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async // [개선] 이 메서드를 비동기로 실행하여 즉시 반환
    @Override
    public void sendVerificationCodeHtml(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("[MoneyWay] 비밀번호 재설정 인증코드");

            Context context = new Context();
            context.setVariable("code", code);
            String html = templateEngine.process("mail/emailcode", context);

            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            // [개선] 더 구체적인 예외를 던져서 에러 원인을 명확히 함
            // ErrorCode에 MAIL_SEND_FAILED 등을 추가하여 사용하면 좋습니다.
            throw new CustomUserException(ErrorCode.UNKNOWN_USER_ERROR); // 예시
        }
    }
}