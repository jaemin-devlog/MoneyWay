package com.example.moneyway.user.repository;

import com.example.moneyway.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByFirebaseUid(String firebaseUid);   // 회원가입 중복 방지용

    Optional<User> findByFirebaseUid(String firebaseUid); // 로그인 이후 사용자 정보 조회
}
