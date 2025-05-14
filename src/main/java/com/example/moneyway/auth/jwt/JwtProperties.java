package com.example.moneyway.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component //스프링 빈으로 등록되어 다른 곳에서 @Autowired 또는 @RequiredArgsConstructor으로 주입받아 사용
@ConfigurationProperties("jwt") //application.yml에 작성된 jwt:설정값을 자동으로 매핑받는 전용 클래스
public class JwtProperties{
    private String issuer; //발급자 정보
    private String secretKey; //서명 비밀 키
}