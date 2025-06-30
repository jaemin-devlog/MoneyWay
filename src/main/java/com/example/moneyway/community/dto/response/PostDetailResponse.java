package com.example.moneyway.community.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 게시글 + 댓글을 통합 조회할 때 사용하는 DTO
 * 게시글 본문과 전체 댓글을 한 번에 반환
 */
@Getter
@Builder
public class PostDetailResponse {
    private PostResponse post;                // 게시글 상세 정보
    private List<CommentResponse> comments;   // 해당 게시글의 댓글 목록
}
