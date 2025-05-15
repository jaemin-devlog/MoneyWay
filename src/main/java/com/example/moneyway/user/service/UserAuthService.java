package com.example.moneyway.user.service;

import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.token.domain.RefreshToken;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.global.exception.CustomException.CustomUserException;
import com.example.moneyway.global.exception.ErrorCode;
import com.example.moneyway.global.util.CookieUtil;
import com.example.moneyway.global.util.TokenUtil;
import com.example.moneyway.user.domain.LoginType;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.dto.request.*;
import com.example.moneyway.user.dto.response.EmailCheckResponse;
import com.example.moneyway.user.dto.response.UserResponse;
import com.example.moneyway.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.util.Optional;

import static com.example.moneyway.global.util.TokenUtil.resolveToken;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserResponse signup(SignupRequest request, HttpServletResponse response) {
        // 1️⃣ 필드 비었는지 확인 (사실 @Valid로도 가능하지만 예외 메시지를 통일하기 위해 추가)
        if (request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank() ||
                request.getNickname() == null || request.getNickname().isBlank()) {
            throw new CustomUserException(ErrorCode.EMPTY_SIGNUP_FIELD);
        }

        // 2️⃣ 이메일 중복
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomUserException(ErrorCode.DUPLICATE_EMAIL);
        }


        // 3️⃣ 닉네임 중복
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomUserException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 4️⃣ 비밀번호 길이 검증
        if (request.getPassword().length() < 8) {
            throw new CustomUserException(ErrorCode.WEAK_PASSWORD);
        }

        // 5️⃣ 이메일이 카카오 계정과 충돌하는 경우(KAKAO/EMAIL 구분 없이 거절)
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomUserException(ErrorCode.DUPLICATE_EMAIL);
        }


        // 6️⃣ 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 7️⃣ 유저 생성
        User user = User.builder()
                .kakaoId("") // 기본 공백 처리
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .loginType(LoginType.EMAIL)
                .build();

        userRepository.save(user);

        // 8️⃣ 토큰 생성
        String accessToken = jwtTokenProvider.generateToken(user, Duration.ofHours(1));
        String refreshToken = jwtTokenProvider.generateToken(user, Duration.ofDays(14));
        refreshTokenRepository.save(new RefreshToken(user, refreshToken));

        // 9️⃣ 쿠키 저장
        CookieUtil.addCookie(response, "access_token", accessToken, 3600);
        CookieUtil.addCookie(response, "refresh_token", refreshToken, 14 * 24 * 60 * 60);

        return UserResponse.from(user);
    }


    public UserResponse login(LoginRequest request, HttpServletResponse response) {
        // 1️⃣ 이메일, 비밀번호 빈 값 확인
        if (request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            throw new CustomUserException(ErrorCode.EMPTY_LOGIN_FIELD);
        }

        // 2️⃣ 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomUserException(ErrorCode.EMAIL_NOT_FOUND));

        // 3️⃣ 로그인 방식 확인
        if (user.getLoginType() != LoginType.EMAIL) {
            throw new CustomUserException(ErrorCode.KAKAO_ACCOUNT_LOGIN);
        }

        // 4️⃣ 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomUserException(ErrorCode.INVALID_PASSWORD);
        }

        // 5️⃣ JWT 토큰 발급
        String accessToken = jwtTokenProvider.generateToken(user, Duration.ofHours(1));
        String refreshToken = jwtTokenProvider.generateToken(user, Duration.ofDays(14));
        refreshTokenRepository.save(new RefreshToken(user, refreshToken));

        // 6️⃣ 쿠키 저장
        CookieUtil.addCookie(response, "access_token", accessToken, 3600);
        CookieUtil.addCookie(response, "refresh_token", refreshToken, 14 * 24 * 60 * 60);

        // 7️⃣ 사용자 정보 반환
        return UserResponse.from(user);
    }

    public void checkPasswordResetEligibility(FindPasswordRequest request) {
        // 1️⃣ 이메일로 사용자 조회 (없으면 예외)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomUserException(ErrorCode.PASSWORD_RESET_EMAIL_NOT_FOUND));

        // 2️⃣ 닉네임이 일치하지 않으면 예외
        if (!user.getNickname().equals(request.getNickname())) {
            throw new CustomUserException(ErrorCode.PASSWORD_RESET_NAME_MISMATCH);
        }

        // 3️⃣ 카카오 로그인 계정이면 비밀번호 재설정 불가
        if (user.getLoginType() != LoginType.EMAIL) {
            throw new CustomUserException(ErrorCode.KAKAO_ACCOUNT_LOGIN);
        }

        // ✅ 통과 시 예외 없이 종료 (비밀번호 재설정 가능)
    }

    public void resetPassword(ResetPasswordRequest request) {
        // 1️⃣ 이메일로 사용자 조회 (없으면 예외)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomUserException(ErrorCode.PASSWORD_RESET_EMAIL_NOT_FOUND));

        // 2️⃣ 카카오 로그인 계정이면 재설정 불가
        if (user.getLoginType() != LoginType.EMAIL) {
            throw new CustomUserException(ErrorCode.KAKAO_ACCOUNT_LOGIN);
        }

        // 3️⃣ 비밀번호 최소 길이 확인
        if (request.getNewPassword().length() < 8) {
            throw new CustomUserException(ErrorCode.WEAK_PASSWORD);
        }

        // 4️⃣ 기존 비밀번호와 동일하면 예외
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new CustomUserException(ErrorCode.PASSWORD_SAME_AS_BEFORE);
        }

        // 5️⃣ 새로운 비밀번호로 암호화 및 저장
        user.resetPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    public EmailCheckResponse findEmailByNickname(FindEmailRequest request) {
        // 1️⃣ 닉네임으로 사용자 조회 (없으면 예외)
        User user = (User) userRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));

        // 2️⃣ 카카오 로그인 계정이면 이메일 조회 불가
        if (user.getLoginType() != LoginType.EMAIL) {
            throw new CustomUserException(ErrorCode.KAKAO_ACCOUNT_LOGIN);
        }

        // 3️⃣ 이메일 반환
        return new EmailCheckResponse(user.getEmail());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1️⃣ 토큰 추출
        String token = resolveToken(request);

        // 2️⃣ 토큰 유효성 검사
        if (!jwtTokenProvider.validToken(token)) {
            throw new CustomUserException(ErrorCode.JWT_INVALID);
        }

        // 3️⃣ 사용자 식별
        Long userId = jwtTokenProvider.getUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));

        // 4️⃣ RefreshToken 삭제
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        // 5️⃣ 쿠키 삭제
        CookieUtil.deleteCookie(request, response, "access_token");
        CookieUtil.deleteCookie(request, response, "refresh_token");
    }

    public void updateNickname(HttpServletRequest request, UpdateNicknameRequest updateRequest) {
        // 1️⃣ 토큰 추출 및 유효성 검증
        String token = TokenUtil.resolveToken(request);
        if (!jwtTokenProvider.validToken(token)) {
            throw new CustomUserException(ErrorCode.JWT_INVALID);
        }

        // 2️⃣ 사용자 식별
        Long userId = jwtTokenProvider.getUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));

        // 3️⃣ 닉네임 중복 체크
        if (userRepository.existsByNickname(updateRequest.getNewNickname())) {
            throw new CustomUserException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 4️⃣ 닉네임 변경
        user.update(updateRequest.getNewNickname());
        userRepository.save(user);
    }

}
