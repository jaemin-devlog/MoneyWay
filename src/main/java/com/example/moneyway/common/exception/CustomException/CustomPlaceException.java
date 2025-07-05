package com.example.moneyway.common.exception.CustomException;

import com.example.moneyway.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 장소(Place) 관련 예외를 처리하는 커스텀 예외 클래스
 */
@Getter
public class CustomPlaceException extends CustomException {

    public CustomPlaceException(ErrorCode errorCode) {
        super(errorCode);
    }
}