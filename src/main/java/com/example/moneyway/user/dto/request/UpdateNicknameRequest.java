package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateNicknameRequest {

    @NotBlank
    private String newNickname;
}
