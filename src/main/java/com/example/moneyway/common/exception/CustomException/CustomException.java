package com.example.moneyway.common.exception.CustomException;

import com.example.moneyway.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 모든 커스텀 예외의 부모 클래스
 * 역할: ErrorCode를 공통으로 관리하여 GlobalExceptionHandler에서 일관된 처리를 가능하게 함
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}