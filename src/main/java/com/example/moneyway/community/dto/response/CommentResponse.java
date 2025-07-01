package com.example.moneyway.community.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글 단건 또는 트리 구조 응답 DTO
 * 대댓글 포함 가능한 재귀 구조로 설계
 */
@Getter
@Builder
public class CommentResponse {
    private Long id;                          // 댓글 ID
    private Long parentId;                    // 부모 댓글 ID (null이면 최상위)
    private Long postId;                      // 댓글이 달린 게시글 ID
    private AuthorDto author;                 // 작성자 정보
    private String content;                   // 댓글 내용
    private LocalDateTime createdAt;          // 생성 시간
    private LocalDateTime updatedAt;          // 수정 시간
    private List<CommentResponse> children;   // 대댓글 목록
}
