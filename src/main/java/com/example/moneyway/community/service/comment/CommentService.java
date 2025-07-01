package com.example.moneyway.community.service.comment;

import com.example.moneyway.community.dto.request.CreateCommentRequest;
import com.example.moneyway.community.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    Long createComment(Long userId, CreateCommentRequest request); // 댓글 생성

    void deleteComment(Long commentId, Long userId); // 댓글 삭제 (소프트 삭제)

    List<CommentResponse> getCommentsByPostId(Long postId); // 게시글 ID로 댓글 목록 조회
}

