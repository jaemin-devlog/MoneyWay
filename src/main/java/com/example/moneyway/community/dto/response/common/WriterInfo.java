package com.example.moneyway.community.dto.response.common;

import com.example.moneyway.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WriterInfo {
    private final Long userId;
    private final String nickname;
    private final String profileImageUrl;

    /**
     * User 엔티티에서 WriterInfo DTO를 생성하는 정적 팩토리 메서드
     */
    public static WriterInfo from(User user) {
        if (user == null) {
            // 사용자가 없는 경우(예: 탈퇴)에 대한 방어 코드
            return WriterInfo.builder()
                    .userId(null)
                    .nickname("알 수 없는 사용자")
                    .profileImageUrl(null)
                    .build();
        }
        return WriterInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}