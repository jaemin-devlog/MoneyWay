package com.example.moneyway.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자 정보 엔티티
 * Setter 없이 비즈니스 메서드로 상태를 제어함
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 생성일/수정일 자동 반영
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(name = "kakao_id", length = 128, unique = true)
    private String kakaoId; // 카카오 고유 ID (일반 로그인은 null)

    @Column(name = "email", unique = true, length = 128)
    private String email; // 이메일 (로그인 ID)

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname; // 닉네임

    @Column(name = "password", length = 100)
    private String password; // 비밀번호 (EMAIL 로그인만 사용)

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl; // 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false, length = 30)
    private LoginType loginType; // 로그인 방식 (EMAIL / KAKAO)

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted; // 탈퇴 여부

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일 (자동 설정)

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일 (자동 설정)

    /**
     * 전체 필드 생성자 (Builder 사용 시 필드 기본값 초기화)
     */
    @Builder
    public User(String email, String password, String nickname, String kakaoId, String profileImageUrl, LoginType loginType) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.kakaoId = kakaoId;
        this.profileImageUrl = profileImageUrl;
        this.loginType = loginType;
        this.isDeleted = false; // 기본값 설정
    }

    //== 정적 팩토리 메서드 ==//

    public static User createEmailUser(String email, String encryptedPassword, String nickname) {
        return User.builder()
                .email(email)
                .password(encryptedPassword)
                .nickname(nickname)
                .loginType(LoginType.EMAIL)
                .build();
    }

    public static User createKakaoUser(String email, String kakaoId, String nickname, String profileImageUrl) {
        return User.builder()
                .email(email)
                .kakaoId(kakaoId)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .loginType(LoginType.KAKAO)
                .build();
    }

    //== 비즈니스 메서드 ==//

    /**
     * 닉네임과 프로필 이미지 수정
     */
    public void updateProfile(String newNickname, String newProfileImageUrl) {
        this.nickname = newNickname;
        this.profileImageUrl = newProfileImageUrl;
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String newEncryptedPassword) {
        this.password = newEncryptedPassword;
    }

    /**
     * 탈퇴 처리 및 개인정보 비식별화
     */
    public void withdraw() {
        this.isDeleted = true;
        this.email = null;
        this.nickname = "탈퇴한 사용자";
        this.kakaoId = null;
        this.profileImageUrl = null;
    }
}
