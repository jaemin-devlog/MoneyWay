package com.example.moneyway.auth.oauth;

import com.example.moneyway.user.domain.LoginType;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class KakaoOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        User user = saveOrUpdate(oAuth2User.getAttributes());
        // ✅ CustomOAuth2User 생성자에 oAuth2User.getAttributes()를 전달하는 것이 좋습니다.
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    /**
     * 카카오 사용자 정보를 추출하여 DB에 저장하거나 업데이트하는 메서드
     */
    private User saveOrUpdate(Map<String, Object> attributes) {
        // Map을 DTO로 변환하여 타입-세이프하게 속성 접근
        KakaoAccount kakaoAccount = objectMapper.convertValue(attributes, KakaoAccount.class);

        String kakaoId = String.valueOf(kakaoAccount.id());
        String email = kakaoAccount.getEmail();
        String nickname = kakaoAccount.getNickname();
        // ✅ [추가] 프로필 이미지 URL 추출
        String profileImageUrl = kakaoAccount.getProfileImageUrl();

        return userRepository.findByKakaoId(kakaoId)
                // ✅ [개선] 기존 사용자인 경우, 닉네임과 프로필 이미지를 최신 정보로 업데이트
                .map(user -> {
                    user.updateProfile(nickname, profileImageUrl);
                    return user;
                })
                // ✅ [개선] 신규 사용자인 경우, User 엔티티의 정적 팩토리 메서드를 사용하여 생성
                .orElseGet(() -> userRepository.save(
                        User.createKakaoUser(email, kakaoId, nickname, profileImageUrl)
                ));
    }
}