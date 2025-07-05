package com.example.moneyway.user.service;

import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 데이터 CRUD 및 비즈니스 로직을 담당하는 서비스
 * (인증/토큰 관련 로직은 포함하지 않음)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 기반 신규 사용자를 생성합니다. (비밀번호 암호화 포함)
     */
    @Transactional
    public User createEmailUser(String email, String rawPassword, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomUserException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomUserException(ErrorCode.DUPLICATE_NICKNAME);
        }

        String encryptedPassword = passwordEncoder.encode(rawPassword);
        User user = User.createEmailUser(email, encryptedPassword, nickname);
        return userRepository.save(user);
    }

    /**
     * ID로 활성화된 사용자를 조회합니다.
     */
    public User findActiveUserById(Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이메일로 사용자를 조회합니다.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 사용자 닉네임을 변경합니다.
     */
    @Transactional
    public void updateNickname(Long userId, String newNickname) {
        if (userRepository.existsByNickname(newNickname)) {
            throw new CustomUserException(ErrorCode.DUPLICATE_NICKNAME);
        }
        User user = findActiveUserById(userId);
        user.updateProfile(newNickname, user.getProfileImageUrl());
    }

    /**
     * 사용자를 탈퇴 처리합니다.
     */
    @Transactional
    public void withdrawUser(Long userId) {
        User user = findActiveUserById(userId);
        user.withdraw(); // User 엔티티의 비즈니스 메서드 호출
    }

    /**
     * 이메일 중복 여부를 확인합니다.
     */
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 닉네임 중복 여부를 확인합니다.
     */
    public boolean checkNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}