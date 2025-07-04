package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 이메일 인증코드 검증 요청 객체
 * 필드: email, code → 인증코드 확인에 사용
 */
@Getter

public class EmailCodeRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "인증코드는 필수 입력값입니다.")
    private String code;
}
