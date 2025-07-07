package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // [개선] final 필드를 위한 생성자 추가
public class SignupRequest {

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private final String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    // 비밀번호 복잡도(영문, 숫자, 특수문자 조합) 검증 강화
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "비밀번호는 8~16자의 영문, 숫자, 특수문자를 포함해야 합니다.")
    private final String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    // 닉네임 길이 제한 추가
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private final String nickname;
}