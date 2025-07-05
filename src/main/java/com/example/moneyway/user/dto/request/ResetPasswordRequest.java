package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // [개선]
public class ResetPasswordRequest {

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private final String email;

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    // 회원가입과 동일한 비밀번호 규칙 적용
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "비밀번호는 8~16자의 영문, 숫자, 특수문자를 포함해야 합니다.")
    private final String newPassword;
}