package com.example.moneyway.community.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 좋아요 요청 DTO
 * 사용자가 특정 게시글에 좋아요를 누를 때 사용
 */
@Getter
@Setter
public class LikeRequest {
    private Long postId;   // 좋아요 대상 게시글 ID
}
