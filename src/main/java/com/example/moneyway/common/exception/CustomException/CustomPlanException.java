package com.example.moneyway.common.exception.CustomException;

import com.example.moneyway.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 여행 계획(Plan) 관련 예외를 처리하는 커스텀 예외 클래스
 */
@Getter
public class CustomPlanException extends CustomException {

    public CustomPlanException(ErrorCode errorCode) {
        super(errorCode);
    }
}