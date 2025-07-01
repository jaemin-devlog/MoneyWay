package com.example.moneyway.community.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 상세 조회 응답 DTO
 * 게시글의 모든 정보를 포함해서 클라이언트에 전달
 */
@Getter
@Builder
public class PostResponse {
    private Long id;                         // 게시글 ID
    private String title;                    // 게시글 제목
    private String content;                  // 게시글 본문
    private AuthorDto author;                // 작성자 정보
    private List<String> imageUrls;          // 게시글에 첨부된 이미지 URL 목록
    private String thumbnailUrl;             // 썸네일 이미지 URL
    private int likeCount;                   // 좋아요 수
    private int scrapCount;                  // 스크랩 수
    private int viewCount;                   // 조회 수
    private LocalDateTime createdAt;         // 게시글 생성 시간
}
