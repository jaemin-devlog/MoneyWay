package com.example.moneyway.common.exception.CustomException;

import com.example.moneyway.common.exception.ErrorCode;

public class CustomGptException extends CustomException {

    public CustomGptException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CustomGptException(ErrorCode errorCode, Throwable cause) {
        super(errorCode);
    }
}