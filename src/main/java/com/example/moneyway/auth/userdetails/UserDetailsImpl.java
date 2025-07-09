package com.example.moneyway.auth.userdetails;

import com.example.moneyway.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security가 사용자를 인증할 때 참조하는 핵심 클래스.
 * User 엔티티를 감싸서 Spring Security가 필요로 하는 정보(아이디, 비밀번호 등)를 제공한다.
 */
@Getter
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Spring Security의 'username'은 우리 시스템의 'email'에 해당합니다.
        return user.getEmail();
    }

    // --- 이하 계정 상태 관련 메서드들 ---

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명(비밀번호)이 만료되지 않았음
    }

    @Override
    public boolean isEnabled() {
        // User 엔티티에 isDeleted 필드가 있다면, 아래와 같이 연동하는 것이 보안상 더 좋습니다.
        // return !user.isDeleted();
        return true; // 계정이 활성화되어 있음
    }
}