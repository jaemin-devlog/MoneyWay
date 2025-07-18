package com.example.moneyway.common.config;

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

    @Bean
    public OpenAPI openAPI() {
        // JWT (bearerAuth)와 쿠키 (cookieAuth)를 모두 사용하는 전역 보안 설정
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityScheme cookieAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("refresh_token");

        // API 문서에 자물쇠 아이콘을 표시하고, 전역적으로 보안 요구사항을 추가
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth")
                .addList("cookieAuth");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerAuth)
                        .addSecuritySchemes("cookieAuth", cookieAuth)
                )
                .security(List.of(securityRequirement))
                .info(new Info()
                        .title("MoneyWay API")
                        .description("여행 예산 플랫폼 MoneyWay Swagger 문서")
                        .version("v1.0"));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return createGroupedApi("admin", "/api/admin/**");
    }

    @Bean
    public GroupedOpenApi aiApi() {
        return createGroupedApi("ai", "/api/ai/**");
    }

    @Bean
    public GroupedOpenApi authApi() {
        return createGroupedApi("auth", "/api/auth/**", "/api/token/**");
    }

    @Bean
    public GroupedOpenApi communityApi() {
        return createGroupedApi("community", "/api/posts/**", "/api/comments/**");
    }

    @Bean
    public GroupedOpenApi placeApi() {
        return createGroupedApi("place", "/api/places/**", "/api/tour/**");
    }

    @Bean
    public GroupedOpenApi planApi() {
        return createGroupedApi("plan", "/api/plans/**");
    }

    @Bean
    public GroupedOpenApi userApi() {
        return createGroupedApi("user", "/api/users/**");
    }

    /**
     * GroupedOpenApi 생성을 위한 private 헬퍼 메소드.
     * 코드 중복을 줄이고 일관성을 유지합니다.
     * @param group 그룹명
     * @param paths 매칭할 경로 패턴 (가변 인자)
     * @return 생성된 GroupedOpenApi 객체
     */
    private GroupedOpenApi createGroupedApi(String group, String... paths) {
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch(paths)
                .build();
    }
}