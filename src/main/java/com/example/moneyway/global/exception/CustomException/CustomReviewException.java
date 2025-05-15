package com.example.moneyway.global.exception.CustomException; // 위치에 따라 조정

import com.example.moneyway.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CustomReviewException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomReviewException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
