package com.example.moneyway.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users") // MySQL에서 'user'는 예약어이므로 'users'로 명시
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 프록시용 기본 생성자
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firebase_uid", nullable = false, unique = true, length = 128)
    private String firebaseUid;

    @Column(name = "kakao_id", unique = true, nullable = true, length = 128) // nullable 명시!
    private String kakaoId;

    @Column(name = "login_type", nullable = false, length = 30)
    private String loginType;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "age_range", length = 20)
    private String ageRange;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        System.out.println("🔥 @PrePersist 호출됨");
        System.out.println("🕒 createdAt: " + createdAt);
        System.out.println("🧾 저장할 사용자: firebaseUid=" + firebaseUid + ", email=" + email + ", nickname=" + nickname);
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
