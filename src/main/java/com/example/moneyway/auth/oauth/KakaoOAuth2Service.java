package com.example.moneyway.auth.oauth;

import com.example.moneyway.user.domain.LoginType;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 카카오 OAuth2 로그인 시, 사용자 정보를 받아와 저장/업데이트 처리하는 서비스
 * 역할: Kakao 인증 서버에서 반환한 사용자 정보를 기반으로 DB에 User 정보를 생성하거나 갱신함
 */
@RequiredArgsConstructor
@Service
public class KakaoOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * ✅ 카카오로부터 사용자 정보를 받아서 User 객체로 매핑 및 저장/업데이트 처리
     * 역할: OAuth2UserRequest로부터 사용자 정보 가져와 saveOrUpdate() 처리 후 반환
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 카카오 사용자 정보 조회 (access_token 포함된 요청 기반)
        OAuth2User user = super.loadUser(userRequest);

        // 사용자 저장 또는 갱신 처리
        saveOrUpdate(user);

        // OAuth2User 반환 (SecurityContext에 저장됨)
        return user;
    }

    /**
     * ✅ 카카오 사용자 정보를 추출하여 기존 DB에 저장된 유저 조회 or 신규 저장
     * 역할: 존재하면 그대로 반환, 존재하지 않으면 새로 생성하여 저장
     */
    private User saveOrUpdate(OAuth2User oAuth2User) {
        // 전체 attributes 받아오기 (JSON 형태)
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 카카오 고유 식별자 추출
        String kakaoId = String.valueOf(attributes.get("id"));

        // 계정 정보(JSON) 추출: email, nickname 등 포함
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        // 프로필 정보 추출
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");

        // DB에서 해당 kakaoId 유저가 존재하는지 확인 후 반환 or 새로 저장
        return userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .kakaoId(kakaoId)
                            .email(email)
                            .nickname(nickname)
                            .loginType(LoginType.KAKAO)
                            .build();
                    return userRepository.save(newUser);
                });
    }
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 카카오 로그인 성공 후 access_token을 포함한 OAuth2UserRequest가 전달됨
 * 2. loadUser()가 실행되며 카카오 API를 통해 사용자 정보를 가져옴
 * 3. saveOrUpdate()를 호출하여 사용자 정보를 기반으로 DB에 사용자 저장/조회
 * 4. 기존 사용자가 없을 경우 새롭게 User 엔티티 생성 후 저장
 * 5. 최종적으로 OAuth2User 객체가 반환되어 SecurityContext에 저장됨
 *
 * → 요약 흐름: 카카오 로그인 성공 → 사용자 정보 조회 → DB 저장/조회 → 인증 완료
 */