package com.example.moneyway.auth.config;

import com.example.moneyway.auth.jwt.JwtAuthenticationFilter;
import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.oauth.OAuth2SuccessHandler;
import com.example.moneyway.auth.oauth.KakaoOAuth2Service;
import com.example.moneyway.auth.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoOAuth2Service kakaoOAuth2Service;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ✅ 모든 요청 허용
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(info -> info
                                .userService(kakaoOAuth2Service))
                        .successHandler(oAuth2SuccessHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (request, response, authException) -> response.setStatus(HttpStatus.UNAUTHORIZED.value())
                        )
                )
                .build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                jwtTokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 기본 strength=10
    }
}
