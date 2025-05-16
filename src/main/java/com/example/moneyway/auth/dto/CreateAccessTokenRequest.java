package com.example.moneyway.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAccessTokenRequest {
    private String refreshToken;
}
