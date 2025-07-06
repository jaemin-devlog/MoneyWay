package com.example.moneyway.common.exception.CustomException;

import com.example.moneyway.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 커뮤니티(Post) 관련 예외를 처리하는 커스텀 예외 클래스
 */
@Getter
public class CustomPostException extends CustomException {

    public CustomPostException(ErrorCode errorCode) {
        super(errorCode);
    }
}