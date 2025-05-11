package com.example.moneyway.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //설정클래스
@Profile("local")  // local 프로필에서만 활성화됨
public class DevCorsConfig { //웹 관련, CORS 관련 설정 담당 클래스

    @Bean
    public WebMvcConfigurer corsConfigurer() {//Spring이 이 메서드를 실행해서  WebMvcConfigurer 객체를 Bean으로 등록
        return new WebMvcConfigurer() { //WebMvcConfigurer은 인터페이스로 여기에서 CORS설정 구현가능
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //addCorsMappings: CORS 정책을 어떻게 적용할지 설정하는 메서드.
                //CorsRegistry는 어떤 경로에 대해 어떤 Origin과 Method 등을 허용할지 지정할 수 있음.
                registry.addMapping("/**")
                        //모든 경로(/api/**, /auth/**, /swagger-ui/** 등 포함)에 CORS 정책을 적용하겠다는 뜻.
                        //실제 서비스에서는 /api/** 정도로 좁히는 게 좋음.
                        .allowedOrigins("*")
                        // localhost:3000, 127.0.0.1, 도메인/포트가 다른 프론트엔드 등 어떤 곳에서 요청이 와도 허용.
                        // 개발용이며, 운영에서는 반드시 제한해야 함.
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")//어떤 HTTP 메서드를 허용할지 지정.
                        .allowedHeaders("*")//요청 헤더를 전부 허용함.
                        .allowCredentials(false);
                //true로 바꾸면 쿠키, Authorization 헤더 등의 정보를 포함할 수 있음.
                //하지만 allowedOrigins("*") 와 allowCredentials(true)는 같이 사용할 수 없음 → CORS 오류 발생
            }
        };
    }
}
