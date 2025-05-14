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

    // 🔐 전체 공통 보안 설정 (쿠키 기반 RefreshToken 인증)
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
                .security(List.of(new SecurityRequirement().addList("cookieAuth")))
                .info(new Info()
                        .title("MoneyWay API")
                        .description("여행 예산 플랫폼 MoneyWay Swagger 문서")
                        .version("v1.0"));
    }

    // ✅ /api/auth/** 엔드포인트 전용 Swagger 그룹
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth-api") // http://localhost:8081/v3/api-docs/auth-api
                .pathsToMatch("/api/auth/**")
                .build();
    }

    // ✅ /api/user/** 전용 Swagger 그룹
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-api")
                .pathsToMatch("/api/user/**")
                .build();
    }

    // ✅ /api/token/** 전용 Swagger 그룹
    @Bean
    public GroupedOpenApi tokenApi() {
        return GroupedOpenApi.builder()
                .group("token-api")
                .pathsToMatch("/api/token/**") // 예: POST /api/token/reissue
                .build();
    }
}
