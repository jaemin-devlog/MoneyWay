package com.example.moneyway.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())  // 🔒 브라우저 팝업 제거
                .formLogin(form -> form.disable())            // 🔒 폼 로그인 제거
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/auth/**"        // ✅ Kakao 인증 콜백, 토큰 발급 경로
                        ).permitAll()
                        .anyRequest().permitAll() // 🔓 추후 authenticated()로 변경 예정
                );
        return http.build();
    }
}
