// ✅ 실제 비밀번호 재설정 요청 DTO
package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 비밀번호를 실제로 변경할 때 사용하는 요청 객체
 * 필드: 이메일, 새로운 비밀번호 → 유효성 검사 포함
 */
@Getter
public class ResetPasswordRequest {

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    private String newPassword;
}

/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. FindPasswordRequest → 이메일/닉네임 일치 여부 확인 (UserAuthService)
 * 2. ResetPasswordRequest → 새 비밀번호 길이/중복 검사 → 암호화 후 저장
 *
 * → eligibility 확인 → 실제 변경 요청 처리 → DB 갱신
 */
