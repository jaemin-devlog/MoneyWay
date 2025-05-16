package com.example.moneyway.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResetPasswordRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String newPassword;
}
