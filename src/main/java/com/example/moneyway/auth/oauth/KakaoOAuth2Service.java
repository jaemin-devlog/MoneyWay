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

@RequiredArgsConstructor
@Service
public class KakaoOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 요청을 바탕으로 유저 정보를 담은 객체 반환
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);
        return user;
    }

    // 유저가 있으면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String kakaoId = String.valueOf(attributes.get("id"));  // ✅ Kakao 고유 ID
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");

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
