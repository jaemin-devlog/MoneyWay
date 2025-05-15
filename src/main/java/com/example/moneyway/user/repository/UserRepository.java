package com.example.moneyway.user.repository;

import com.example.moneyway.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ 로그인용 (일반 로그인 사용자)
    Optional<User> findByEmail(String email);

    // ✅ 카카오 로그인용
    Optional<User> findByKakaoId(String kakaoId);

    // ✅ 중복 체크
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Optional<Object> findByNickname(@NotBlank String nickname);
}
