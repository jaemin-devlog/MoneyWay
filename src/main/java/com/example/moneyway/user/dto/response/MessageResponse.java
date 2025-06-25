package com.example.moneyway.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// MessageResponse: 단순 메시지 응답 (성공/실패 등 상태 전달)
public class MessageResponse {
    private final String message;
}
