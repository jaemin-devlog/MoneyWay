package com.example.moneyway.user.service;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.common.util.CookieUtil;
import com.example.moneyway.common.util.TokenUtil;
import com.example.moneyway.user.domain.LoginType;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.dto.request.*;
import com.example.moneyway.user.dto.response.UserResponse;
import com.example.moneyway.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.example.moneyway.common.util.TokenUtil.resolveToken;

/**
 * 일반 회원가입, 로그인, 비밀번호 재설정, 로그아웃, 닉네임 변경 등 인증 핵심 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * ✅ 회원가입 처리 메서드
     * 흐름: 유효성 검사 → 중복 확인 → User 생성 → JWT 발급/저장 → 쿠키 저장
     */
    public UserResponse signup(SignupRequest request, HttpServletResponse response) {
        // 필수 필드 유효성 검사
        if (request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank() ||
                request.getNickname() == null || request.getNickname().isBlank()) {
            throw new CustomUserException(ErrorCode.EMPTY_SIGNUP_FIELD);
        }

        // 중복 검사
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomUserException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomUserException(ErrorCode.DUPLICATE_NICKNAME);
        }
        if (request.getPassword().length() < 8) {
            throw new CustomUserException(ErrorCode.WEAK_PASSWORD);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성 및 저장
        User user = User.ofEmail(
                request.getEmail(),
                encodedPassword,
                request.getNickname()
        );

        System.out.println("nickname >>> " + request.getNickname());
        System.out.println("email >>> " + request.getEmail());

        userRepository.save(user);

        // JWT 발급 및 쿠키/DB 저장
        String accessToken = jwtTokenProvider.generateToken(user, Duration.ofHours(1));
        String refreshToken = jwtTokenProvider.generateToken(user, Duration.ofDays(14));

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.save(new RefreshToken(user, refreshToken));

        CookieUtil.addCookie(response, "access_token", accessToken, 3600);
        CookieUtil.addCookie(response, "refresh_token", refreshToken, 14 * 24 * 60 * 60);

        return UserResponse.from(user);
    }

    /**
     * ✅ 로그인 처리 메서드
     * 흐름: 사용자 조회 → 비밀번호 확인 → JWT 발급 + 저장
     */
    public UserResponse login(LoginRequest request, HttpServletResponse response) {
        if (request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            throw new CustomUserException(ErrorCode.EMPTY_LOGIN_FIELD);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomUserException(ErrorCode.EMAIL_NOT_FOUND));

        if (user.getLoginType() != LoginType.EMAIL) {
            throw new CustomUserException(ErrorCode.KAKAO_ACCOUNT_LOGIN);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomUserException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.generateToken(user, Duration.ofHours(1));
        String refreshToken = jwtTokenProvider.generateToken(user, Duration.ofDays(14));

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.save(new RefreshToken(user, refreshToken));

        CookieUtil.addCookie(response, "access_token", accessToken, 3600);
        CookieUtil.addCookie(response, "refresh_token", refreshToken, 14 * 24 * 60 * 60);

        return UserResponse.from(user);
    }

    /**
     * ✅ 비밀번호 재설정 가능 여부 확인
     * 조건: 이메일 존재 + 닉네임 일치 + EMAIL 로그인 타입
     */
    public void checkPasswordResetEligibility(FindPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomUserException(ErrorCode.PASSWORD_RESET_EMAIL_NOT_FOUND));

        if (!user.getNickname().equals(request.getNickname())) {
            throw new CustomUserException(ErrorCode.PASSWORD_RESET_NAME_MISMATCH);
        }

        if (user.getLoginType() != LoginType.EMAIL) {
            throw new CustomUserException(ErrorCode.KAKAO_ACCOUNT_LOGIN);
        }
    }

    /**
     * ✅ 실제 비밀번호 재설정 수행
     * 조건: 기존 비밀번호와 다르고, 8자 이상이어야 함
     */
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomUserException(ErrorCode.PASSWORD_RESET_EMAIL_NOT_FOUND));

        if (user.getLoginType() != LoginType.EMAIL) {
            throw new CustomUserException(ErrorCode.KAKAO_ACCOUNT_LOGIN);
        }

        if (request.getNewPassword().length() < 8) {
            throw new CustomUserException(ErrorCode.WEAK_PASSWORD);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new CustomUserException(ErrorCode.PASSWORD_SAME_AS_BEFORE);
        }

        user.resetPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * ✅ 로그아웃 처리
     * 동작: RefreshToken 제거, 쿠키 삭제
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = resolveToken(request);

        if (!jwtTokenProvider.validToken(token)) {
            throw new CustomUserException(ErrorCode.JWT_INVALID);
        }

        Long userId = jwtTokenProvider.getUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        CookieUtil.deleteCookie(request, response, "access_token");
        CookieUtil.deleteCookie(request, response, "refresh_token");
    }

    /**
     * ✅ 닉네임 변경 처리
     * 조건: JWT 인증된 사용자 → 중복 검사 → 변경 및 저장
     */
    public void updateNickname(HttpServletRequest request, UpdateNicknameRequest updateRequest) {
        String token = TokenUtil.resolveToken(request);
        if (!jwtTokenProvider.validToken(token)) {
            throw new CustomUserException(ErrorCode.JWT_INVALID);
        }

        Long userId = jwtTokenProvider.getUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));

        if (userRepository.existsByNickname(updateRequest.getNewNickname())) {
            throw new CustomUserException(ErrorCode.DUPLICATE_NICKNAME);
        }

        user.update(updateRequest.getNewNickname());
        userRepository.save(user);
    }
}

/**
 * ✅ 전체 동작 흐름 요약
 *
 * 1. 회원가입/로그인 → 사용자 유효성 확인 → 토큰 발급 및 저장 → 쿠키 저장
 * 2. 비밀번호 찾기 → 이메일+닉네임 확인 → 비밀번호 재설정
 * 3. 로그아웃 → 토큰 제거 → 쿠키 삭제
 * 4. 닉네임 변경 → 토큰 인증 → 사용자 식별 → 중복 확인 후 저장
 *
 * → 유효성 검사 → 사용자 인증 → 토큰/쿠키 처리 → 응답 반환
 */
