package com.example.moneyway.community.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 게시글 목록 조회용 응답 DTO
 * 목록 형태로 요약된 게시글 정보 전달
 */
@Getter
@Builder
public class PostSummaryResponse {
    private Long id;                     // 게시글 ID
    private String title;                // 게시글 제목
    private AuthorDto author;            // 작성자 정보
    private String thumbnailUrl;         // 썸네일 이미지 URL
    private int likeCount;               // 좋아요 수
    private int commentCount;            // 댓글 수
    private LocalDateTime createdAt;     // 게시글 작성일
}
