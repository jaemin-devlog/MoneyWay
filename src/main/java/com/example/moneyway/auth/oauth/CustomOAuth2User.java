package com.example.moneyway.auth.oauth;

import com.example.moneyway.user.domain.User;
import lombok.Getter; // [개선] Getter 추가
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 기본 OAuth2User에 우리 시스템의 User 엔티티를 포함시킨 커스텀 객체.
 * 역할: 인증 과정에서 DB에서 조회한 User 정보를 다음 단계(SuccessHandler)로 전달.
 */
@Getter // [개선] SuccessHandler에서 user 필드에 쉽게 접근할 수 있도록 Getter 추가
public class CustomOAuth2User implements OAuth2User {

    private final User user; // 우리 시스템의 User 엔티티
    private final Map<String, Object> attributes; // OAuth 공급자로부터 받은 원본 속성

    /**
     * [개선] 생성자 변경: User와 attributes를 직접 받도록 수정하여 클래스의 독립성 향상
     */
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        // ✅ 저장된 원본 속성 반환
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ✅ 모든 소셜 로그인 사용자에게 기본 권한("ROLE_USER") 부여
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        // ✅ 카카오 응답의 고유 ID를 반환 (application.yml의 user-name-attribute 값)
        return String.valueOf(attributes.get("id"));
    }
}