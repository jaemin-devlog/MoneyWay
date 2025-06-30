package com.example.moneyway.community.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 스크랩(북마크) 요청 DTO
 * 사용자가 특정 게시글을 저장(스크랩)할 때 사용
 */
@Getter
@Setter
public class ScrapRequest {
    private Long postId;   // 스크랩할 대상 게시글 ID
}
