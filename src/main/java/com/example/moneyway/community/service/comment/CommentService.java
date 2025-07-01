package com.example.moneyway.community.service.comment;

import com.example.moneyway.community.dto.request.CreateCommentRequest;
import com.example.moneyway.community.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    /**
     * 댓글 생성
     * @param userId 댓글 작성자 ID
     * @param request 댓글 생성 요청 정보
     * @return 생성된 댓글 ID
     */
    Long createComment(Long userId, CreateCommentRequest request);

    /**
     * 특정 게시글의 댓글 목록 조회
     * @param postId 게시글 ID
     * @return 댓글 응답 DTO 리스트
     */
    List<CommentResponse> getCommentsByPostId(Long postId);

    /**
     * 댓글 삭제
     * @param commentId 댓글 ID
     * @param userId 요청자 ID
     */
    void deleteComment(Long commentId, Long userId);
}
