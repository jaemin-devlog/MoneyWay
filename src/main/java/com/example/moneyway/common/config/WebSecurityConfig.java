package com.example.moneyway.common.config;

import com.example.moneyway.auth.jwt.JwtAuthenticationFilter;
import com.example.moneyway.auth.oauth.KakaoOAuth2Service;
import com.example.moneyway.auth.oauth.OAuth2LoginFailureHandler;
import com.example.moneyway.auth.oauth.OAuth2SuccessHandler;
import com.example.moneyway.auth.oauth.repository.OAuth2AuthorizationCookieRepository;
import com.example.moneyway.common.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

    // 현재 가지고 계신 모든 필수 컴포넌트를 생성자 주입으로 받습니다.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final KakaoOAuth2Service kakaoOAuth2Service;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final OAuth2AuthorizationCookieRepository oAuth2AuthorizationCookieRepository;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

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
                        // 인증 없이 접근을 허용할 경로들
                        .requestMatchers(
                                "/api/auth/**",       // 인증(로그인, 회원가입) 관련 API
                                "/login/**",          // 소셜 로그인 리다이렉션 경로
                                "/oauth2/**",         // OAuth2 처리 경로
                                "/error",             // Spring Boot 기본 에러 페이지
                                // Swagger UI 접근 허용
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**"
                        ).permitAll()
                        // 위 경로를 제외한 모든 요청은 반드시 인증을 거쳐야 합니다.
                        .anyRequest().authenticated()
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestRepository(oAuth2AuthorizationCookieRepository))
                        .userInfoEndpoint(info -> info
                                .userService(kakaoOAuth2Service))
                        // 직접 만든 성공 및 실패 핸들러를 등록합니다.
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가합니다.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 인증 예외 발생 시, 커스텀 EntryPoint가 일관된 에러 응답을 처리합니다.
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 허용할 프론트엔드 출처(Origin) 목록
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://moneyway-572cf.web.app"));
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 허용할 HTTP 헤더
        configuration.setAllowedHeaders(List.of("*"));
        // 쿠키 등 자격 증명을 포함한 요청 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 대해 위 CORS 설정을 적용합니다.
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}