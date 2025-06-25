package com.example.moneyway.user.controller;

import com.example.moneyway.user.dto.request.*;
import com.example.moneyway.user.dto.response.EmailResultResponse;
import com.example.moneyway.user.dto.response.MessageResponse;
import com.example.moneyway.user.dto.response.UserResponse;
import com.example.moneyway.user.service.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 일반 이메일/비밀번호 기반 인증을 처리하는 REST 컨트롤러
 * 역할: 회원가입, 로그인, 비밀번호 재설정, 로그아웃, 닉네임 변경 등 유저 인증 API 제공
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserAuthController {

    private final UserAuthService userAuthService;

    /**
     * ✅ 회원가입 요청 핸들러
     * 입력: SignupRequest(email, password, nickname)
     * 처리: 유효성 검사 → 저장 → 토큰 발급 및 쿠키 저장
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request,
                                               HttpServletResponse response) {
        UserResponse userResponse = userAuthService.signup(request, response);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * ✅ 로그인 요청 핸들러
     * 입력: LoginRequest(email, password)
     * 처리: 사용자 확인 → 비밀번호 일치 → 토큰 발급 + 쿠키 저장
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        UserResponse userResponse = userAuthService.login(request, response);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * ✅ 비밀번호 재설정 전, 사용자 정보 확인용 요청
     * 입력: FindPasswordRequest(email, nickname)
     * 처리: 이메일 존재 여부 + 닉네임 일치 여부 확인
     */
    @PostMapping("/password/find")
    public ResponseEntity<MessageResponse> checkPasswordReset(@Valid @RequestBody FindPasswordRequest request) {
        userAuthService.checkPasswordResetEligibility(request);
        return ResponseEntity.ok(new MessageResponse("비밀번호 재설정이 가능합니다."));
    }

    /**
     * ✅ 실제 비밀번호 재설정 요청
     * 입력: ResetPasswordRequest(email, newPassword)
     * 처리: 기존 비밀번호와 다를 경우에만 업데이트
     */
    @PatchMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userAuthService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("비밀번호가 성공적으로 재설정되었습니다."));
    }

    /**
     * ✅ 로그아웃 요청
     * 처리: RefreshToken DB에서 삭제 + 쿠키 초기화
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        userAuthService.logout(request, response);
        return ResponseEntity.ok(new MessageResponse("성공적으로 로그아웃되었습니다."));
    }

    /**
     * ✅ 닉네임 변경 요청
     * 입력: UpdateNicknameRequest(newNickname)
     * 처리: 인증된 사용자 확인 → 중복 검사 → 변경 후 저장
     */
    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponse> updateNickname(HttpServletRequest request,
                                                          @Valid @RequestBody UpdateNicknameRequest updateRequest) {
        userAuthService.updateNickname(request, updateRequest);
        return ResponseEntity.ok(new MessageResponse("닉네임이 성공적으로 변경되었습니다."));
    }
}

/**
 * ✅ 전체 흐름 요약
 *
 * 1. 클라이언트 요청 수신 (회원가입, 로그인 등)
 * 2. Request DTO에 바인딩 및 유효성 검사
 * 3. UserAuthService에 위임하여 비즈니스 로직 처리
 * 4. 필요한 경우 토큰 발급, 쿠키 저장, DB 변경 수행
 * 5. UserResponse 또는 MessageResponse 형태로 응답 반환
 *
 * → REST 엔드포인트 → 유효성 검사 → Service 호출 → 응답 처리
 */
