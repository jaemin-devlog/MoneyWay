package com.example.moneyway.user.controller;


import com.example.moneyway.user.dto.request.EmailCodeRequest;
import com.example.moneyway.user.dto.request.EmailRequest;
import com.example.moneyway.user.dto.response.MessageResponse;
import com.example.moneyway.user.service.EmailCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/password")
@RequiredArgsConstructor
public class EmailCodeController {

    private final EmailCodeService emailCodeService;

    // ✅ 인증코드 이메일 전송
    @PostMapping("/send-code")
    public ResponseEntity<MessageResponse> sendVerificationCode(@RequestBody EmailRequest request) {
        emailCodeService.sendCode(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("인증코드가 이메일로 전송되었습니다."));
    }

    // ✅ 인증코드 검증
    @PostMapping("/verify-code")
    public ResponseEntity<MessageResponse> verifyCode(@RequestBody EmailCodeRequest request) {
        emailCodeService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(new MessageResponse("이메일 인증이 완료되었습니다."));
    }
}
