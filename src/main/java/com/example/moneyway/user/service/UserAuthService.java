package com.example.moneyway.user.service;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.common.util.CookieUtil;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    // ========================== 회원가입 ==========================
    public UserResponse signup(SignupRequest request, HttpServletResponse response) {
        // [개선] 기본적인 필드 유효성 검사는 Controller의 @Valid에 위임
        // 비즈니스 규칙(중복, 비밀번호 정책) 검증에 집중
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomUserException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomUserException(ErrorCode.DUPLICATE_NICKNAME);
        }
        if (request.getPassword().length() < 8) {
            throw new CustomUserException(ErrorCode.WEAK_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.ofEmail(request.getEmail(), encodedPassword, request.getNickname());
        userRepository.save(user);

        // 토큰 생성 및 쿠키 추가 로직은 공통 메서드로 분리 가능
        addTokensToCookie(user, response);

        return UserResponse.from(user);
    }

    // ========================== 로그인 ==========================
    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomUserException(ErrorCode.EMAIL_NOT_FOUND));

        if (user.getLoginType() != LoginType.EMAIL) {
            throw new CustomUserException(ErrorCode.KAKAO_ACCOUNT_LOGIN);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomUserException(ErrorCode.INVALID_PASSWORD);
        }

        addTokensToCookie(user, response);
        return UserResponse.from(user);
    }

    // ========================== 로그아웃 ==========================
    /**
     * [수정] HttpServletRequest 대신 userId를 직접 받아 처리
     */
    public void logout(Long userId, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.findActiveById(userId); // 활성화된 유저인지 확인
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

        CookieUtil.deleteCookie(request, response, "access_token");
        CookieUtil.deleteCookie(request, response, "refresh_token");
    }

    // ========================== 닉네임 변경 ==========================
    /**
     * [수정] HttpServletRequest 대신 userId를 직접 받아 처리
     */
    public void updateNickname(Long userId, UpdateNicknameRequest request) {
        // 닉네임 중복 검사
        if (userRepository.existsByNickname(request.getNewNickname())) {
            throw new CustomUserException(ErrorCode.DUPLICATE_NICKNAME);
        }
        // 사용자 조회 및 닉네임 변경
        User user = userService.findActiveById(userId);
        user.update(request.getNewNickname());
        // @Transactional에 의해 변경 감지(dirty checking)되어 자동으로 DB에 반영
    }

    // ========================== 회원 탈퇴 ==========================
    /**
     * [수정] HttpServletRequest 대신 userId를 직접 받아 처리
     */
    public void withdrawUser(Long userId) {
        // 실제 탈퇴 로직은 UserService에 위임되어 있으므로 호출만 수행
        userService.withdrawUser(userId);
    }

    // ========================== 비밀번호 재설정 ==========================
    @Transactional(readOnly = true)
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
    }

    // ========================== 중복 확인 ==========================
    @Transactional(readOnly = true)
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean checkNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // ========================== Helper 메서드 ==========================
    private void addTokensToCookie(User user, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.generateToken(user, Duration.ofHours(1));
        String refreshToken = jwtTokenProvider.generateToken(user, Duration.ofDays(14));

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.save(new RefreshToken(user, refreshToken));

        CookieUtil.addCookie(response, "access_token", accessToken, 3600);
        CookieUtil.addCookie(response, "refresh_token", refreshToken, 14 * 24 * 60 * 60);
    }
}