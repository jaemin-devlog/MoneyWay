// ✅ 회원가입 요청 DTO
package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 클라이언트에서 회원가입 시 입력한 정보 요청 객체
 * 필드: 이메일, 비밀번호, 닉네임 → 유효성 검사 포함
 */
@Getter
public class SignupRequest {

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;
}