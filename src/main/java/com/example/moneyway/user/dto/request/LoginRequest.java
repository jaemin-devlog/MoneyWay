// ✅ 로그인 요청 DTO
package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 클라이언트에서 로그인 시 입력한 정보 요청 객체
 * 필드: 이메일, 비밀번호 → 유효성 검사 포함
 */
@Getter
public class LoginRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
/**
 * ✅ 전체 동작 흐름 설명
 *
 * 1. 사용자가 클라이언트에서 회원가입 또는 로그인 폼 작성
 * 2. 폼 내용이 JSON으로 변환되어 컨트롤러에 전달됨
 * 3. DTO가 @RequestBody로 바인딩되고, @Valid로 유효성 검사 진행됨
 * 4. 문제가 없으면 UserAuthService로 전달되고, 있으면 예외 처리기로 전달됨
 *
 * → 폼 입력 → DTO 바인딩 → 유효성 검증 → 서비스 레이어 호출
 */
