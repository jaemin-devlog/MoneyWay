package com.example.moneyway.auth.oauth;

import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.repository.UserRepository;
import com.example.moneyway.user.service.AvatarService;
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
    private final AvatarService avatarService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        User user = saveOrUpdate(oAuth2User.getAttributes());

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(Map<String, Object> attributes) {
        KakaoAccount kakaoAccount = objectMapper.convertValue(attributes, KakaoAccount.class);

        String kakaoId = String.valueOf(kakaoAccount.id());
        String email = kakaoAccount.getEmail();
        String nickname = kakaoAccount.getNickname();
        String profileImageUrl = kakaoAccount.getProfileImageUrl();

        return userRepository.findByKakaoId(kakaoId)
                .map(user -> {
                    user.updateNickname(nickname);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        user.updateProfileImage(profileImageUrl);
                    }
                    return user;
                })
                .orElseGet(() -> {
                    // ✅ [수정] 프로필 이미지가 없을 때, 닉네임 대신 이메일로 기본 이미지 생성
                    String finalProfileImageUrl = (profileImageUrl != null && !profileImageUrl.isEmpty())
                            ? profileImageUrl
                            : avatarService.generateAvatar(email);

                    User newUser = User.createKakaoUser(email, kakaoId, nickname, finalProfileImageUrl);
                    return userRepository.save(newUser);
                });
    }
}