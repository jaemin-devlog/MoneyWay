package com.example.moneyway.user.repository;

import com.example.moneyway.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 정보를 DB에서 조회하고 저장하는 JPA 리포지토리
 * 역할: 이메일, 닉네임, ID, KakaoId 기준으로 사용자 엔티티를 찾거나 중복 여부 확인
 */
public interface UserRepository extends JpaRepository<User, Long> {

    //이메일로 사용자 조회 (일반 로그인용)
    Optional<User> findByEmail(String email);

    //카카오 고유 ID로 사용자 조회 (카카오 로그인용)
    Optional<User> findByKakaoId(String kakaoId);

    //이메일 중복 여부 확인
    boolean existsByEmail(String email);

    //닉네임 중복 여부 확인
    boolean existsByNickname(String nickname);

    // 닉네임으로 사용자 조회
    Optional<User> findByNickname(@NotBlank String nickname);
}

/**
 * ✅ 전체 동작 흐름 요약
 *
 * 1. 회원가입 시 이메일/닉네임 중복 체크에 사용됨
 * 2. 로그인 시 이메일로 사용자 정보 조회
 * 3. 카카오 로그인 시 kakaoId로 사용자 조회
 * 4. 비밀번호 재설정 시 닉네임 일치 여부 확인
 *
 * → 다양한 식별자 기준으로 사용자 조회 + 중복 검사 지원
 */
