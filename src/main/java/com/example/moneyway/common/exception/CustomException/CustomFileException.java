package com.example.moneyway.common.exception.CustomException;

import com.example.moneyway.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CustomFileException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * ✅ [수정] 이 생성자가 기존에 있었을 것입니다.
     * @param errorCode 발생한 에러의 종류
     */
    public CustomFileException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * ✅ [추가] 이 생성자를 새로 추가해주세요.
     * 원래 발생했던 예외(cause)를 함께 기록하여 디버깅을 용이하게 합니다.
     * @param errorCode 발생한 에러의 종류
     * @param cause 근본 원인이 되는 예외
     */
    public CustomFileException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}