package com.example.moneyway.user.service;

import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    //사용자 ID로 조회 (없으면 USER_NOT_FOUND 예외)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));
    }

    //이메일로 조회 (없으면 USER_NOT_FOUND 예외)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomUserException(ErrorCode.USER_NOT_FOUND));
    }
}
