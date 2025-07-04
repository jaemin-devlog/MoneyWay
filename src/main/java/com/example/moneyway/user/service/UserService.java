package com.example.moneyway.user.service;

import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 사용자 조회 전용 서비스
 * 역할: 사용자 ID 또는 이메일로 사용자 정보 조회 + 유효성 검증
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * ✅ 이메일 기반 사용자 조회
     * 사용처: 로그인, 비밀번호 재설정, Kakao OAuth2 로그인 후 사용자 확인
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * ✅ ID 기반 사용자 조회
     * 사용처: JWT에서 userId 추출 후 사용자 정보 조회할 때 사용
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));
    }

    public User findActiveById(Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));
    }

    public String getNicknameById(Long userId) {
        return findById(userId).getNickname(); // 위에서 정의한 findById 재활용
    }

    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new CustomUserException(ErrorCode.ALREADY_WITHDRAWN);
        }

        user.setDeleted(true);
        userRepository.save(user);
    }




}

/**
 * ✅ 전체 동작 흐름 요약
 *
 * 1. 이메일/ID를 기반으로 사용자 존재 여부 확인
 * 2. 존재하지 않으면 USER_NOT_FOUND 예외 발생
 * 3. 존재하면 User 엔티티 반환하여 인증/조회 흐름에 연결
 *
 * → 사용자 정보 식별 → DB 조회 → 예외 또는 반환
 */
