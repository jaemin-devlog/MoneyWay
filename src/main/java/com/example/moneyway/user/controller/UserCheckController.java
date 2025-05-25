package com.example.moneyway.user.controller;

import com.example.moneyway.user.dto.response.CheckResponse;
import com.example.moneyway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 이메일/닉네임 중복 여부를 확인하는 API 컨트롤러
 * 역할: 회원가입 시 사전 체크를 위한 엔드포인트 제공
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/check")
public class UserCheckController {

    private final UserRepository userRepository;

    /**
     * ✅ 이메일 중복 확인 API
     * GET /api/users/check/email?email=example@email.com
     * 반환: exists 여부(Boolean)
     */
    @GetMapping("/email")
    public ResponseEntity<CheckResponse> checkEmail(@RequestParam String email) {
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(new CheckResponse(exists));
    }

    /**
     * ✅ 닉네임 중복 확인 API
     * GET /api/users/check/nickname?nickname=재민
     * 반환: exists 여부(Boolean)
     */
    @GetMapping("/nickname")
    public ResponseEntity<CheckResponse> checkNickname(@RequestParam String nickname) {
        boolean exists = userRepository.existsByNickname(nickname);
        return ResponseEntity.ok(new CheckResponse(exists));
    }
}

/**
 * ✅ 전체 동작 흐름 요약
 *
 * 1. 클라이언트에서 회원가입 폼 입력 중 이메일/닉네임 중복 여부를 비동기로 확인
 * 2. 해당 API는 UserRepository의 existsBy 메서드를 통해 Boolean 값 반환
 * 3. 응답은 CheckResponse(exists) 형식으로 클라이언트에 제공됨
 *
 * → GET 요청 → DB 중복 검사 → 결과 응답 (exists: true/false)
 */
