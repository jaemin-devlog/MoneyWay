package com.example.moneyway.user.controller;

import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.dto.request.*;
import com.example.moneyway.user.dto.response.*;
import com.example.moneyway.user.service.EmailCodeService;
import com.example.moneyway.user.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserAuthService userAuthService;
    private final EmailCodeService emailCodeService;

    // ========================= 🔐 사용자 인증/정보 =========================

    /**
     * ✅ 내 정보 조회
     * @AuthenticationPrincipal을 통해 인증된 사용자 객체를 직접 받아 사용합니다.
     * Spring Security가 토큰 검증 및 사용자 조회를 모두 처리해줍니다.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * ✅ 닉네임 변경
     */
    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponse> updateNickname(@AuthenticationPrincipal User user,
                                                          @Valid @RequestBody UpdateNicknameRequest request) {
        userAuthService.updateNickname(user.getId(), request);
        return ResponseEntity.ok(new MessageResponse("닉네임이 성공적으로 변경되었습니다."));
    }

    /**
     * ✅ 회원 탈퇴
     */
    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdrawUser(@AuthenticationPrincipal User user) {
        userAuthService.withdrawUser(user.getId());
        return ResponseEntity.ok(new MessageResponse("회원 탈퇴가 완료되었습니다."));
    }

    // ========================= 📝 회원가입/로그인/로그아웃 =========================

    /**
     * ✅ 회원가입
     * @Valid를 통해 Request DTO의 유효성 검사를 수행합니다.
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request,
                                               HttpServletResponse response) {
        UserResponse userResponse = userAuthService.signup(request, response);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * ✅ 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        UserResponse userResponse = userAuthService.login(request, response);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * ✅ 로그아웃
     * [개선] @AuthenticationPrincipal을 사용하여 일관성을 유지합니다.
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@AuthenticationPrincipal User user,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        userAuthService.logout(user.getId(), request, response);
        return ResponseEntity.ok(new MessageResponse("성공적으로 로그아웃되었습니다."));
    }

    // ========================= ✅ 이메일/닉네임 중복 체크 =========================

    /**
     * ✅ 이메일 중복 확인
     */
    @GetMapping("/check/email")
    public ResponseEntity<CheckResponse> checkEmail(@RequestParam String email) {
        boolean exists = userAuthService.checkEmailExists(email);
        return ResponseEntity.ok(new CheckResponse(exists));
    }

    /**
     * ✅ 닉네임 중복 확인
     */
    @GetMapping("/check/nickname")
    public ResponseEntity<CheckResponse> checkNickname(@RequestParam String nickname) {
        boolean exists = userAuthService.checkNicknameExists(nickname);
        return ResponseEntity.ok(new CheckResponse(exists));
    }

    // ========================= 🔐 이메일 인증 및 비밀번호 재설정 =========================

    /**
     * ✅ 비밀번호 재설정 코드 발송
     */
    @PostMapping("/password/send-code")
    public ResponseEntity<MessageResponse> sendVerificationCode(@Valid @RequestBody EmailRequest request) {
        emailCodeService.sendCode(request.getEmail());
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
     * ✅ 비밀번호 재설정 가능 여부 확인
     */
    @PostMapping("/password/find")
    public ResponseEntity<MessageResponse> checkPasswordReset(@Valid @RequestBody FindPasswordRequest request) {
        userAuthService.checkPasswordResetEligibility(request);
        return ResponseEntity.ok(new MessageResponse("비밀번호 재설정이 가능합니다."));
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