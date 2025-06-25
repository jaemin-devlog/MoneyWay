// ✅ 비밀번호 재설정 eligibility 확인 요청 DTO
package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 비밀번호 재설정 가능 여부를 확인하기 위한 요청 객체
 * 필드: 이메일, 닉네임 → 사용자 존재 및 일치 여부 확인용
 */
@Getter
public class FindPasswordRequest {

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;
}
