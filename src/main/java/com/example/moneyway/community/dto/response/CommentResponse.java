package com.example.moneyway.community.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 댓글 응답 DTO
 *
 * - 게시글에 달린 댓글 목록 조회 시 사용
 * - 대댓글 기능은 제외된 flat 구조
 */
@Getter
@Builder
public class CommentResponse {

    private Long commentId;            // 댓글 ID
    private String content;            // 댓글 내용
    private String writerNickname;     // 작성자 닉네임
    private String writerProfileUrl;   // 작성자 프로필 이미지 URL
    private LocalDateTime createdAt;   // 작성 시간
}
