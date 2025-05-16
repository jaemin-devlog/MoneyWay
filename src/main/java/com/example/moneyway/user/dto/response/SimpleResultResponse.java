package com.example.moneyway.user.dto.response;

import com.example.moneyway.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SimpleResultResponse {
    private final Long id;
    private final String email;
    private final String nickname;

    @Builder
    public SimpleResultResponse(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }

    public static SimpleResultResponse from(User user) {
        return SimpleResultResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
