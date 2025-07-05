package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // [개선]
public class UpdateNicknameRequest {

    @NotBlank(message = "새 닉네임은 필수입니다.")
    // 회원가입과 동일한 닉네임 규칙 적용
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private final String newNickname;
}