package com.example.moneyway.user.controller;

import com.example.moneyway.user.dto.request.*;
import com.example.moneyway.user.dto.response.CheckResponse;
import com.example.moneyway.user.dto.response.MessageResponse;
import com.example.moneyway.user.service.EmailCodeService;
import com.example.moneyway.user.service.UserAuthService;
import com.example.moneyway.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증이 필요 없는 사용자 관련 유틸리티 API를 전담하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserUtilController {

    private final UserService userService;
    private final EmailCodeService emailCodeService;
    private final UserAuthService userAuthService;

    /**
     * ✅ 이메일 중복 확인
     */
    @GetMapping("/check/email")
    public ResponseEntity<CheckResponse> checkEmail(@RequestParam String email) {
        boolean exists = userService.checkEmailExists(email);
        return ResponseEntity.ok(new CheckResponse(exists));
    }

    /**
     * ✅ 닉네임 중복 확인
     */
    @GetMapping("/check/nickname")
    public ResponseEntity<CheckResponse> checkNickname(@RequestParam String nickname) {
        boolean exists = userService.checkNicknameExists(nickname);
        return ResponseEntity.ok(new CheckResponse(exists));
    }

    /**
     * ✅ 비밀번호 재설정 코드 발송
     */
    @PostMapping("/password/send-code")
    public ResponseEntity<MessageResponse> sendPasswordResetCode(@Valid @RequestBody EmailRequest request) {
        userAuthService.sendPasswordResetCode(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("인증코드가 이메일로 전송되었습니다."));
    }

    /**
     * ✅ 인증코드 검증
     */
    @PostMapping("/password/verify-code")
    public ResponseEntity<MessageResponse> verifyCode(@Valid @RequestBody EmailCodeRequest request) {
        emailCodeService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(new MessageResponse("이메일 인증이 완료되었습니다."));
    }

    /**
     * ✅ 실제 비밀번호 재설정
     */
    @PatchMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userAuthService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("비밀번호가 성공적으로 재설정되었습니다."));
    }
}