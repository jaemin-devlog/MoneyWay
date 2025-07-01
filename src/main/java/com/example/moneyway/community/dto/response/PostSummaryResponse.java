package com.example.moneyway.community.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 게시글 목록 요약 응답 DTO
 *
 * - 게시글 리스트에서 간단히 보여줄 정보를 담습니다.
 * - 썸네일, 제목, 작성자 정보, 집계 필드 중심 구성입니다.
 */
@Getter
@Builder
public class PostSummaryResponse {

    private Long postId;               // 게시글 ID
    private String title;              // 제목
    private String thumbnailUrl;       // 썸네일 이미지 URL
    private Boolean isChallenge;       // 챌린지 여부 (챌린지 탭 필터링용)

    private Integer likeCount;         // 좋아요 수
    private Integer commentCount;      // 댓글 수
    private Integer scrapCount;        // 스크랩 수

    private String writerNickname;     // 작성자 닉네임
    private String writerProfileUrl;   // 작성자 프로필 이미지 URL
    private LocalDateTime createdAt;   // 작성 시간
}
