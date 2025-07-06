package com.example.moneyway.community.dto.response.common;

import lombok.Builder;
import lombok.Getter;

/**
 * 작성자 정보를 담는 공통 DTO
 */
@Getter
@Builder
public class WriterInfo {
    private final Long userId;
    private final String nickname;
    // private final String profileImageUrl; // 향후 프로필 이미지 추가 확장 가능
}