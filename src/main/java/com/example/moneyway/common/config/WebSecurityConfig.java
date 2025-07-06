package com.example.moneyway.common.config;

import com.example.moneyway.auth.jwt.JwtAuthenticationFilter;
import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.oauth.OAuth2SuccessHandler;
import com.example.moneyway.auth.oauth.KakaoOAuth2Service;
import com.example.moneyway.auth.oauth.repository.OAuth2AuthorizationCookieRepository;
import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.common.exception.CustomAuthenticationEntryPoint; // [신규]
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // [신규]
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // [신규]
import org.springframework.web.cors.CorsConfigurationSource; // [신규]
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // [신규]

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoOAuth2Service kakaoOAuth2Service;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    // [신규] OAuth2 로그인 실패 시 리다이렉트 될 URL을 application.yml에서 주입
    @Value("${oauth.login-failure-redirect-uri:http://localhost:3000/login?error}")
    private String loginFailureRedirectUri;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // [개선] CORS 설정 추가
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // API 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 아래 경로들은 인증 없이 접근 허용
                        .requestMatchers(
                                "/api/auth/**", "/login/**", "/oauth2/**", "/api/**",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
                                "/error" // Spring Boot 기본 에러 페이지
                        ).permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(info -> info
                                .userService(kakaoOAuth2Service))
                        .successHandler(oAuth2SuccessHandler())
                        // 하드코딩된 URL 대신 주입받은 값 사용
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect(loginFailureRedirectUri);
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // 일관된 예외 응답을 위해 커스텀 EntryPoint 사용
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                )
                .build();
    }

    /**
     * CORS(Cross-Origin Resource Sharing) 설정
     * 다른 도메인의 프론트엔드에서 API를 호출할 수 있도록 허용
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 허용할 출처(프론트엔드 주소) 설정
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://192.168.100.37:3000","https://moneyway-572cf.web.app"));
        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 허용할 HTTP 헤더 설정
        configuration.setAllowedHeaders(List.of("*"));
        // 자격 증명(쿠키 등) 허용 여부
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 대해 위 설정 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
                oAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }

    @Bean
    public OAuth2AuthorizationCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationCookieRepository();
    }

    /**
     * [신규] 인증 실패 시 일관된 ErrorResponse를 반환하기 위한 커스텀 EntryPoint
     */
    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
}