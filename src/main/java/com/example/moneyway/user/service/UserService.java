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
 * 사용자 데이터 CRUD 및 핵심 비즈니스 로직을 담당하는 서비스
 * (인증, 마이페이지 액션 관련 로직은 포함하지 않음)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;

    @Transactional
    public User createEmailUser(String email, String rawPassword, String nickname) {
        validateNewUser(email, nickname);

        String encryptedPassword = passwordEncoder.encode(rawPassword);
        String profileImageUrl = avatarService.generateAvatar(email);

        User user = User.createEmailUser(email, encryptedPassword, nickname, profileImageUrl);
        return userRepository.save(user);
    }

    private void validateNewUser(String email, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomUserException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomUserException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public User findActiveUserById(Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));
    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}