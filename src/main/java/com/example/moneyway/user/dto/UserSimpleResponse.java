package com.example.moneyway.user.dto;

import com.example.moneyway.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSimpleResponse {
    private final Long id;
    private final String email;
    private final String nickname;

    @Builder
    public UserSimpleResponse(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }

    public static UserSimpleResponse from(User user) {
        return UserSimpleResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
