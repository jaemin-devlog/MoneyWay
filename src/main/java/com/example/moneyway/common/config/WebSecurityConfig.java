package com.example.moneyway.common.config;

import com.example.moneyway.auth.jwt.JwtAuthenticationFilter;
import com.example.moneyway.auth.jwt.JwtTokenProvider;
import com.example.moneyway.auth.oauth.OAuth2SuccessHandler;
import com.example.moneyway.auth.oauth.KakaoOAuth2Service;
import com.example.moneyway.auth.oauth.repository.OAuth2AuthorizationCookieRepository;
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
                .csrf(AbstractHttpConfigurer::disable) //CSRF비활성화
                .httpBasic(AbstractHttpConfigurer::disable) //브라우저 팝업 제거
                .formLogin(AbstractHttpConfigurer::disable)//Form Login 제거
                .logout(AbstractHttpConfigurer::disable) // 세션기반이 아니므로 서버가 로그아웃 처리 필요X
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                //세션 생성, 저장X ->JWT는 요청마다 인증 정보를 포함 -> 서버에 세션 저장소 필요X
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() //현재는 모든 요청을 허용 -> 실 운영에서는 분리
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        //인가 요청 정보를 세션이 아니라 쿠키에 저장하기 위해 설정
                        .userInfoEndpoint(info -> info
                                .userService(kakaoOAuth2Service)) //카카오에서 사용자 정보를 받아서 DB저장
                        .successHandler(oAuth2SuccessHandler())   //OAuth2 인증 후 -> AccessToken, RefreshToken 발급 -> 쿠키로 응답
                        .failureHandler((request, response, exception) -> {
                            // 로그인 실패 시 프론트 로그인 에러 페이지로 리다이렉트
                            response.sendRedirect("http://192.168.100.37:3000/login?error");
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // Filter보다 앞단에서 JWT를 먼저 검사하도록 설정
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                (request, response, authException) -> response.setStatus(HttpStatus.UNAUTHORIZED.value())
                        )
                ) //JWT가 유효하지 않거나 로그인되지 않은 상태에서 인증 필요한 요청 보낼 경우 401에러
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
    public OAuth2AuthorizationCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationCookieRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 기본 strength=10
    }
}
