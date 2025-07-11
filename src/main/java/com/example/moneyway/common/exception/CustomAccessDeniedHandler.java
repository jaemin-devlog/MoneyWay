package com.example.moneyway.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 인증은 되었으나, 해당 리소스에 대한 접근 권한이 없는 경우(403 Forbidden)
 * 일관된 에러 응답(ErrorResponse)을 반환하는 클래스
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("접근 권한 없음(403 Forbidden) - 요청 URI: {}, 사용자: {}", request.getRequestURI(), request.getRemoteUser());

        // ErrorResponse 객체 생성
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.FORBIDDEN_USER);

        // 응답 상태 및 콘텐츠 타입 설정
        response.setStatus(errorResponse.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // ErrorResponse 객체를 JSON 문자열로 변환하여 응답 본문에 작성
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}