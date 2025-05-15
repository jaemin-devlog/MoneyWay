package com.example.moneyway.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 카카오 전용 ID (nullable)
    @Column(name = "kakao_id", unique = true, length = 128, nullable = true)
    private String kakaoId;

    // ✅ 이메일 로그인 or 카카오 공통 이메일
    @Column(name = "email", unique = true, length = 128)
    private String email;

    // ✅ 닉네임 (공통 필드, 고유값)
    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    // ✅ 일반 로그인 유저만 사용하는 비밀번호 (nullable)
    @Column(name = "password", length = 100)
    private String password;

    // ✅ 카카오 로그인 시 프로필 이미지
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // ✅ 로그인 방식: EMAIL / KAKAO
    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false, length = 30)
    private LoginType loginType;


    // ✅ 생성일 / 수정일
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ✅ 생성일 자동 세팅
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ✅ 수정일 자동 세팅
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ 닉네임 변경 로직
    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    // ✅ 비밀번호 재설정 로직 (null-safe)
    public void resetPassword(String newPassword) {
        this.password = newPassword;
    }

}
