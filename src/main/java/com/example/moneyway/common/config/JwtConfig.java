package com.example.moneyway.common.config;

import com.example.moneyway.auth.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class) // JwtProperties를 Bean으로 등록하고 활성화
public class JwtConfig {
}