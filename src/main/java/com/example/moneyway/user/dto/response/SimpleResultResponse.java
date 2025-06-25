package com.example.moneyway.user.dto.response;

import com.example.moneyway.user.domain.User;
import lombok.Builder;
import lombok.Getter;
// ✅ 간단한 사용자 응답 DTO (예: 리스트나 요약용)
//SimpleResultResponse: 간단한 유저 요약 정보 반환용 (목록 등)
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
