package com.example.moneyway.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자 정보 엔티티
 * 필드: 이메일/닉네임/비밀번호 + 카카오 ID + 로그인 타입 + 생성일/수정일 포함
 * 기능: 닉네임 변경, 비밀번호 재설정 지원
 */
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

    // ✅ 카카오 고유 식별자 (일반 로그인 시 비어 있음)
    @Column(name = "kakao_id", length = 128, nullable = true)
    private String kakaoId;

    // ✅ 이메일: 로그인 식별자 (일반 로그인과 Kakao 공통)
    @Column(name = "email", unique = true, length = 128)
    private String email;

    // ✅ 닉네임: 사용자 표시명 (중복 불가)
    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    // ✅ 비밀번호 (EMAIL 로그인만 사용, Kakao 로그인은 null)
    @Column(name = "password", length = 100)
    private String password;

    // ✅ 프로필 이미지 URL (카카오 로그인용)
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // ✅ 로그인 유형: EMAIL or KAKAO
    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false, length = 30)
    private LoginType loginType;

    // ✅ 생성/수정 시간 자동 설정
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * ✅ 회원가입 - 이메일 기반 사용자 팩토리 메서드
     */
    public static User ofEmail(String email, String password, String nickname) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .loginType(LoginType.EMAIL)
                .build();
    }

    /**
     * ✅ 회원가입 - 카카오 로그인 사용자 팩토리 메서드
     */
    public static User ofKakao(String email, String kakaoId, String nickname, String profileImageUrl) {
        return User.builder()
                .email(email)
                .kakaoId(kakaoId)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .loginType(LoginType.KAKAO)
                .build();
    }

    /**
     * ✅ 닉네임 변경 로직
     */
    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    /**
     * ✅ 비밀번호 재설정 로직
     */
    public void resetPassword(String newPassword) {
        this.password = newPassword;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
