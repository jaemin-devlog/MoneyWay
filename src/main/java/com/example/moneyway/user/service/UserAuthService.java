package com.example.moneyway.user.service;

import com.example.moneyway.auth.dto.TokenInfo;
import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.user.domain.LoginType;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.dto.request.FindPasswordRequest;
import com.example.moneyway.user.dto.request.LoginRequest;
import com.example.moneyway.user.dto.request.ResetPasswordRequest;
import com.example.moneyway.user.dto.request.SignupRequest;
import com.example.moneyway.user.dto.response.AuthResponse;
import com.example.moneyway.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * 회원가입, 로그인, 비밀번호 재설정 등 사용자 인증 흐름을 전담하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthService {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 이메일 기반 회원가입을 처리하고, 토큰과 사용자 정보를 반환합니다.
     */
    public AuthResponse signup(SignupRequest request) {
        User user = userService.createEmailUser(request.getEmail(), request.getPassword(), request.getNickname());
        TokenInfo tokenInfo = generateTokens(user);

        // [수정] record의 접근자 메서드(refreshToken()) 사용
        saveOrUpdateRefreshToken(user, tokenInfo.refreshToken());

        return AuthResponse.builder()
                .tokenInfo(tokenInfo)
                .userInfo(UserResponse.from(user))
                .build();
    }

    /**
     * 이메일 기반 로그인을 처리하고, 토큰과 사용자 정보를 반환합니다.
     */
    public AuthResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomUserException(ErrorCode.INVALID_PASSWORD);
        }

        TokenInfo tokenInfo = generateTokens(user);
        // [수정] record의 접근자 메서드(refreshToken()) 사용
        saveOrUpdateRefreshToken(user, tokenInfo.refreshToken());

        return AuthResponse.builder()
                .tokenInfo(tokenInfo)
                .userInfo(UserResponse.from(user))
                .build();
    }

    /**
     * 비밀번호 재설정 자격(이메일-닉네임 일치)을 확인합니다.
     */
    @Transactional(readOnly = true)
    public void checkPasswordResetEligibility(FindPasswordRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (!user.getNickname().equals(request.getNickname())) {
            throw new CustomUserException(ErrorCode.PASSWORD_RESET_NAME_MISMATCH);
        }
        if (user.getLoginType() != LoginType.EMAIL) {
            throw new CustomUserException(ErrorCode.KAKAO_ACCOUNT_LOGIN);
        }
    }

    /**
     * 실제 비밀번호를 재설정합니다.
     */
    public void resetPassword(ResetPasswordRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (user.getLoginType() != LoginType.EMAIL) {
            throw new CustomUserException(ErrorCode.KAKAO_ACCOUNT_LOGIN);
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new CustomUserException(ErrorCode.PASSWORD_SAME_AS_BEFORE);
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    /**
     * 사용자에 대한 AccessToken과 RefreshToken을 생성합니다. (private)
     */
    private TokenInfo generateTokens(User user) {
        String accessToken = tokenProvider.generateToken(user, Duration.ofHours(1));
        String refreshToken = tokenProvider.generateToken(user, Duration.ofDays(14));

        // [수정] record는 builder 대신 생성자를 직접 호출
        return new TokenInfo("Bearer", accessToken, refreshToken);
    }

    /**
     * 사용자의 RefreshToken을 DB에 저장하거나 업데이트합니다. (private)
     */
    private void saveOrUpdateRefreshToken(User user, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .map(token -> token.update(newRefreshToken))
                .orElse(new RefreshToken(user, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }
    /**
     * ✅ [추가] 로그아웃을 처리합니다.
     * 서버에 저장된 사용자의 Refresh Token을 삭제합니다.
     * @param email 현재 인증된 사용자의 이메일
     */
    public void logout(String email) {
        User user = userService.findByEmail(email);
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(refreshTokenRepository::delete);
    }
}