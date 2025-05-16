package com.example.moneyway.user.controller;

import com.example.moneyway.user.dto.request.*;
import com.example.moneyway.user.dto.response.EmailCheckResponse;
import com.example.moneyway.user.dto.response.MessageResponse;
import com.example.moneyway.user.dto.response.UserResponse;
import com.example.moneyway.user.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request,
                                               HttpServletResponse response) {
        UserResponse userResponse = userAuthService.signup(request, response);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        UserResponse userResponse = userAuthService.login(request, response);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/password/find")
    public ResponseEntity<MessageResponse> checkPasswordReset(@Valid @RequestBody FindPasswordRequest request) {
        userAuthService.checkPasswordResetEligibility(request);
        return ResponseEntity.ok(new MessageResponse("비밀번호 재설정이 가능합니다."));

    }

    @PatchMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userAuthService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("비밀번호가 성공적으로 재설정되었습니다."));
    }

    @PostMapping("/email/find")
    public ResponseEntity<EmailCheckResponse> findEmail(@Valid @RequestBody FindEmailRequest request) {
        return ResponseEntity.ok(userAuthService.findEmailByNickname(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        userAuthService.logout(request, response);
        return ResponseEntity.ok(new MessageResponse("성공적으로 로그아웃되었습니다."));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponse> updateNickname(HttpServletRequest request,
                                                          @Valid @RequestBody UpdateNicknameRequest updateRequest) {
        userAuthService.updateNickname(request, updateRequest);
        return ResponseEntity.ok(new MessageResponse("닉네임이 성공적으로 변경되었습니다."));
    }

}
