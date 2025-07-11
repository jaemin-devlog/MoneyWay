package com.example.moneyway.common.exception.CustomException;

import com.example.moneyway.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 장바구니(Cart) 관련 예외를 처리하는 커스텀 예외 클래스
 */
@Getter
public class CustomCartException extends CustomException {

    public CustomCartException(ErrorCode errorCode) {
        super(errorCode);
    }
}