package com.example.moneyway.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// ✅ 중복 여부 응답 DTO (Check용)
//CheckResponse: 중복 여부(Boolean) 반환용 (이메일/닉네임 중복 체크)
public class CheckResponse {
    private final boolean exists;
}