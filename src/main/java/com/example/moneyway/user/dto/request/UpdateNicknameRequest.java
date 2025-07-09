package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor; // 변경

@Getter
@NoArgsConstructor
public class UpdateNicknameRequest {

    @NotBlank(message = "새 닉네임은 필수입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String newNickname;
}