package com.example.moneyway.community.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 댓글 또는 대댓글 작성 요청 DTO
 * 댓글 내용과 대상 게시글/부모 댓글 정보를 포함
 */
@Getter
@Setter
public class CreateCommentRequest {
    private Long postId;       // 댓글이 달릴 대상 게시글 ID
    private String content;    // 댓글 내용
}
