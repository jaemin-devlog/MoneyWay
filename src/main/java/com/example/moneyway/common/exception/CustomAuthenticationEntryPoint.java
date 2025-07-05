package com.example.moneyway.common.exception;

import com.example.moneyway.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증되지 않은 사용자의 접근 시, 일관된 에러 응답(ErrorResponse)을 반환하는 클래스
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // ErrorResponse 객체 생성
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED_USER);

        // 응답 상태 및 콘텐츠 타입 설정
        response.setStatus(errorResponse.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // ErrorResponse 객체를 JSON 문자열로 변환하여 응답 본문에 작성
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}