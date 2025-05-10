package com.example.moneyway.user.dto;

import com.example.moneyway.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter // 모든 필드에 대한 Getter 자동 생성
public class UserResponse {

    private final String firebaseUid;       // Firebase 인증 UID (기본 식별자)
    private final String kakaoId;           // Kakao 로그인 시 받은 사용자 고유 ID
    private final String email;             // 사용자 이메일
    private final String nickname;          // 사용자 닉네임
    private final String profileImageUrl;   // 프로필 사진 URL
    private final String gender;            // 성별 (male / female / unknown)
    private final String ageRange;          // 연령대 (예: "20~29")
    private final String loginType;         // 로그인 타입 (예: "KAKAO", "GOOGLE")

    // Builder 패턴을 이용한 생성자 정의
    @Builder
    public UserResponse(String firebaseUid, String kakaoId, String email, String nickname,
                        String profileImageUrl, String gender, String ageRange, String loginType) {
        this.firebaseUid = firebaseUid;
        this.kakaoId = kakaoId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.gender = gender;
        this.ageRange = ageRange;
        this.loginType = loginType;
    }

    // User 엔티티 객체로부터 UserResponse DTO를 생성하는 정적 메서드
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .firebaseUid(user.getFirebaseUid())              // Firebase UID 복사
                .kakaoId(user.getKakaoId())                      // Kakao ID 복사
                .email(user.getEmail())                          // 이메일 복사
                .nickname(user.getNickname())                    // 닉네임 복사
                .profileImageUrl(user.getProfileImageUrl())      // 프로필 이미지 복사
                .gender(user.getGender())                        // 성별 복사
                .ageRange(user.getAgeRange())                    // 연령대 복사
                .loginType(user.getLoginType())                  // 로그인 타입 복사
                .build();
    }
}
