package com.example.moneyway.common.exception;

import com.example.moneyway.common.exception.CustomException.CustomException; // [개선] 공통 부모 예외
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1️⃣ 모든 사용자 정의 예외 처리 핸들러
     * CustomException을 상속하는 모든 예외(CustomUserException, CustomPostException 등)를 처리
     */
    @ExceptionHandler(CustomException.class) // [개선] 부모 클래스를 처리
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("CustomException 발생: {}", errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    /**
     * 2️⃣ @Valid 유효성 검증 실패 시 처리 핸들러
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("Validation failed: {}", errorMessage);
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .status(400)
                        .code("VALIDATION_ERROR")
                        .message(errorMessage)
                        .build());
    }

    /**
     * 3️⃣ 나머지 모든 예외 처리 (최후의 보루)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled Exception: {} at {}", ex.getMessage(), request.getRequestURI(), ex);
        return ResponseEntity.status(500)
                .body(ErrorResponse.builder()
                        .status(500)
                        .code("INTERNAL_SERVER_ERROR")
                        .message("서버 내부 오류가 발생했습니다.")
                        .build());
    }
}