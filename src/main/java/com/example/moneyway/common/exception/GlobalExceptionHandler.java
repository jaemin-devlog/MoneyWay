package com.example.moneyway.common.exception;

import com.example.moneyway.common.exception.CustomException.CustomUserException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 클래스
 * 역할: 커스텀 예외, 유효성 검증 실패, 서버 오류 등 모든 예외를 통합 처리
 * Swagger 환경에서는 제외되도록 @Profile("!swagger") 적용
 */
//@Profile("!swagger") // ✅ Swagger 환경에서는 제외
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1️⃣ 사용자 정의 예외 처리 핸들러
     * CustomUserException 발생 시 ErrorCode 기반의 응답 생성
     */
    @ExceptionHandler(CustomUserException.class)
    public ResponseEntity<ErrorResponse> handleCustomUserException(CustomUserException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus()) // HttpStatus 코드 설정
                .body(ErrorResponse.of(errorCode)); // ErrorResponse 포맷으로 응답
    }

    /**
     * 2️⃣ 서버 내부 공통 예외 처리
     * Exception 발생 시 Swagger 요청은 패스하고, 나머지는 500 응답 반환
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) throws Exception {
        String path = request.getRequestURI();

        // Swagger 관련 요청이면 예외를 던져 Swagger 내부 처리에 맡김
        if (path.startsWith("/swagger") || path.startsWith("/v3/api-docs")) {
            throw ex;
        }

        // 기타 예외는 공통 형식으로 500 응답
        return ResponseEntity.status(500)
                .body(ErrorResponse.builder()
                        .status(500)
                        .code("INTERNAL_SERVER_ERROR")
                        .message("서버 내부 오류가 발생했습니다.")
                        .build());
    }

    /**
     * 3️⃣ @Valid 유효성 검증 실패 시 처리 핸들러
     * 첫 번째 오류 메시지를 클라이언트에 반환
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(); // 첫 오류 메시지 추출

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .status(400)
                        .code("VALIDATION_ERROR")
                        .message(errorMessage)
                        .build());
    }
}
