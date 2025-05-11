package com.example.moneyway.auth.oauth;

import com.example.moneyway.auth.dto.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private final WebClient webClient;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // 1. 인가 코드로 AccessToken 요청
    public String getAccessToken(String code) {
        Map<String, Object> response = webClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(
                        "grant_type=authorization_code" +
                                "&client_id=" + clientId +
                                "&redirect_uri=" + redirectUri +
                                "&code=" + code
                )
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        log.info("🔑 Kakao AccessToken 응답: {}", response);
        return (String) response.get("access_token");
    }

    // 2. AccessToken으로 사용자 정보 요청
    public KakaoUserInfo getUserInfo(String accessToken) {
        Map<String, Object> response = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me") // 사용자 정보 API는 kauth가 아니라 kapi!
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        log.info("👤 Kakao 사용자 정보 응답: {}", response);

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return KakaoUserInfo.builder()
                .kakaoId(String.valueOf(response.get("id")))
                .email((String) kakaoAccount.get("email"))
                .nickname((String) profile.get("nickname"))
                .build();
    }
}
