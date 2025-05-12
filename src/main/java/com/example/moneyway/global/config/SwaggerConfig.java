package com.example.moneyway.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    // 1) 기본 OpenAPI 정의 (기본 문서)
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("cookieAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name("refreshToken")
                        )
                )
                // 전역으로 cookieAuth 보안 적용
                .security(List.of(new SecurityRequirement().addList("cookieAuth")))
                .info(new Info()
                        .title("MoneyWay API")
                        .description("여행 예산 플랫폼 MoneyWay Swagger 문서")
                        .version("v1.0")
                );
    }

    // 2) /auth/** 엔드포인트를 위한 별도 그룹 정의
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth-api")                // → /v3/api-docs/auth-api로 노출
                .pathsToMatch("/auth/**")         // 이 그룹에 포함할 경로
                .build();
    }

    // 3) 필요하다면 user-api 같은 추가 그룹도 정의 가능
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-api")
                .pathsToMatch("/user/**")
                .build();
    }
}
