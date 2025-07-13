package com.example.moneyway.common.exception.CustomException;

import com.example.moneyway.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 사용자(User) 관련 예외를 처리하는 커스텀 예외 클래스
 */
@Getter
public class CustomUserException extends CustomException {

    public CustomUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}