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
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("cookieAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("refresh_token")
                        )
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                .security(List.of(
                        new SecurityRequirement().addList("cookieAuth"),
                        new SecurityRequirement().addList("bearerAuth")
                ))
                .info(new Info()
                        .title("MoneyWay API")
                        .description("여행 예산 플랫폼 MoneyWay Swagger 문서")
                        .version("v1.0"));
    }


    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth-api")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-api")
                .pathsToMatch("/api/users/**")
                .build();
    }
    @Bean
    public GroupedOpenApi planApi() {
        return GroupedOpenApi.builder()
                .group("plan-api")
                .pathsToMatch("/plans/**")  // 또는 "/api/plans/**"
                .build();
    }
    @Bean
    public GroupedOpenApi tokenApi() {
        return GroupedOpenApi.builder()
                .group("token-api")
                .pathsToMatch("/api/token/**")
                .build();
    }
}
