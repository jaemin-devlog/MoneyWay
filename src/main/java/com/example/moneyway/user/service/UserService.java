package com.example.moneyway.user.service;

import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.dto.FirebaseSignupRequest;
import com.example.moneyway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public void registerUser(String firebaseUid, FirebaseSignupRequest request) {
        boolean exists = userRepository.existsByFirebaseUid(firebaseUid);
        System.out.println("🧪 중복 검사 결과: " + exists);

        if (exists) {
            System.out.println("⛔ 이미 존재하는 사용자입니다. 저장을 건너뜁니다.");
            return;
        }

        User user = User.builder()
                .firebaseUid(firebaseUid)
                .kakaoId(request.getKakaoId())
                .email(request.getEmail())
                .nickname(request.getNickname())
                .profileImageUrl(request.getProfileImageUrl())
                .gender(request.getGender())
                .ageRange(request.getAgeRange())
                .loginType("KAKAO")
                .build();

        userRepository.save(user);
        userRepository.flush();

        System.out.println("🔥 저장 대상 UID: " + firebaseUid);
        System.out.println("🔥 저장 객체: " + user);
    }

    public User getMyProfile(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }
}
