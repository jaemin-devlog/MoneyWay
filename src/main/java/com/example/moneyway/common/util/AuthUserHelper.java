package com.example.moneyway.common.util;

import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserHelper {

    private final UserService userService;

    /**
     * Spring Security의 UserDetails 객체에서 시스템의 User 엔티티를 조회합니다.
     * @param principal @AuthenticationPrincipal로 주입된 사용자 정보
     * @return 조회된 User 엔티티
     */
    public User getUser(UserDetails principal) {
        if (principal == null) {
            // 이 로직이 호출되는 시점에는 항상 인증된 사용자이므로 null이 될 수 없지만, 방어적으로 코딩합니다.
            throw new SecurityException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return userService.findByEmail(principal.getUsername());
    }
}