package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor; // 변경

@Getter
@NoArgsConstructor // 변경
public class EmailCodeRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email; // final 제거

    @NotBlank(message = "인증코드는 필수 입력값입니다.")
    private String code; // final 제거
}