package com.example.moneyway.common.config;

import com.example.moneyway.auth.jwt.JwtAuthenticationFilter;
import com.example.moneyway.auth.oauth.KakaoOAuth2Service;
import com.example.moneyway.auth.oauth.OAuth2LoginFailureHandler;
import com.example.moneyway.auth.oauth.OAuth2SuccessHandler;
import com.example.moneyway.auth.oauth.repository.OAuth2AuthorizationCookieRepository;
import com.example.moneyway.common.exception.CustomAccessDeniedHandler;
import com.example.moneyway.common.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final KakaoOAuth2Service kakaoOAuth2Service;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final OAuth2AuthorizationCookieRepository oAuth2AuthorizationCookieRepository;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. 인증 없이 접근을 허용할 경로들
                        .requestMatchers(
                                // Swagger UI and API docs
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                // Auth and OAuth endpoints
                                "/api/auth/signup", // 회원가입
                                "/api/auth/login",  // 로그인
                                "/login/**",         // 소셜 로그인 과정
                                "/oauth2/**",        // 소셜 로그인 과정
                                "/error"             // 에러 페이지
                        ).permitAll()
                        // 2. 그 외 모든 요청은 반드시 인증을 거쳐야 함
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestRepository(oAuth2AuthorizationCookieRepository))
                        .userInfoEndpoint(info -> info
                                .userService(kakaoOAuth2Service))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 인증/인가 예외 처리 핸들러 등록
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 인증 실패(401)
                        .accessDeniedHandler(customAccessDeniedHandler)         // 인가 실패(403)
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://moneyway-572cf.web.app", "http://localhost:8081"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}