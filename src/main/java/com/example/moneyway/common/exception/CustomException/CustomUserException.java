package com.example.moneyway.common.exception.CustomException; // 위치에 따라 조정

import com.example.moneyway.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CustomUserException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomUserException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}