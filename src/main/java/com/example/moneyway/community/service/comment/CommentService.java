package com.example.moneyway.community.service.comment;

import com.example.moneyway.community.dto.request.CreateCommentRequest;
import com.example.moneyway.community.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    Long createComment(Long userId, CreateCommentRequest request);

    void deleteComment(Long commentId, Long userId);

    // [반영] 메서드 이름 변경
    List<CommentResponse> getActiveCommentsByPostId(Long postId);
}