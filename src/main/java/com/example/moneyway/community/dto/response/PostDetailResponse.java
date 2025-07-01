package com.example.moneyway.community.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 상세 응답 DTO
 *
 * - 게시글 본문, 작성자 정보, 통계 정보 등을 포함한 전체 상세 데이터
 */
@Getter
@Builder
public class PostDetailResponse {

    private Long postId;               // 게시글 ID
    private String title;              // 제목
    private String content;            // 본문 내용
    private Integer totalCost;         // 총 지출 비용
    private Boolean isChallenge;       // 챌린지 여부
    private String thumbnailUrl;       // 썸네일 이미지 URL
    private List<String> imageUrls;    // 첨부 이미지 URL 리스트

    private Integer likeCount;         // 좋아요 수
    private Integer commentCount;      // 댓글 수
    private Integer scrapCount;        // 스크랩 수
    private Integer viewCount;         // 조회수

    private LocalDateTime createdAt;   // 작성 시간

    private String writerNickname;     // 작성자 닉네임
    private String writerProfileUrl;   // 작성자 프로필 이미지 URL
}
