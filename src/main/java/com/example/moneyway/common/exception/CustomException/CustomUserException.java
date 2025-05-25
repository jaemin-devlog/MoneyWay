
// ✅ 사용자 정의 예외 클래스
package com.example.moneyway.common.exception.CustomException;

import com.example.moneyway.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 비즈니스 로직에서 발생하는 예외를 표현하는 사용자 정의 예외 클래스
 * 역할: ErrorCode를 함께 던져 전역 예외 처리기에서 일관된 응답 포맷을 제공할 수 있게 함
 */
@Getter
public class CustomUserException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomUserException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 서비스/컨트롤러 계층에서 문제가 발생하면 CustomUserException을 throw함
 * 2. 생성 시 ErrorCode를 전달하여 예외 상태와 메시지를 설정함
 * 3. 전역 예외 처리기(@ControllerAdvice 등)에서 해당 예외를 감지하여 공통 에러 응답을 반환함
 *
 * → 요약 흐름: 에러 발생 → CustomUserException 발생 → ErrorCode 기반 응답 제어
 */
