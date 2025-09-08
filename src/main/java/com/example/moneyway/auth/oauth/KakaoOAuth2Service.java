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

        // 1. 카카오 ID로 사용자 조회
        return userRepository.findByKakaoId(kakaoId)
                .map(user -> {
                    // 1-1. 사용자가 존재하면 정보 업데이트
                    String uniqueNickname = nickname; // 카카오에서 받은 닉네임으로 초기화
                    if (!user.getNickname().equals(uniqueNickname) && userRepository.existsByNickname(uniqueNickname)) {
                        while (userRepository.existsByNickname(uniqueNickname)) {
                            uniqueNickname = nickname + (int)(Math.random() * 10000);
                        }
                    }
                    user.updateNickname(uniqueNickname);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        user.updateProfileImage(profileImageUrl);
                    }
                    return user;
                })
                .orElseGet(() -> {
                    // 2. 카카오 ID로 사용자를 찾을 수 없는 경우, 이메일로 조회
                    return userRepository.findByEmail(email)
                            .map(user -> {
                                // 2-1. 이메일로 사용자가 존재하면 카카오 계정 연결
                                user.linkKakaoAccount(kakaoId);
                                // 카카오 프로필 정보로 업데이트도 가능 (선택적)
                                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                    user.updateProfileImage(profileImageUrl);
                                }
                                return user;
                            })
                            .orElseGet(() -> {
                                // 3. 카카오 ID와 이메일로 모두 사용자를 찾을 수 없으면 신규 생성
                                String finalProfileImageUrl = (profileImageUrl != null && !profileImageUrl.isEmpty())
                                        ? profileImageUrl
                                        : avatarService.generateAvatar(email);

                                String uniqueNickname = nickname;
                                while (userRepository.existsByNickname(uniqueNickname)) {
                                    uniqueNickname = nickname + (int)(Math.random() * 10000);
                                }

                                User newUser = User.createKakaoUser(email, kakaoId, uniqueNickname, finalProfileImageUrl);
                                return userRepository.save(newUser);
                            });
                });
    }
}