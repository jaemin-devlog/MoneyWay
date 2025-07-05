package com.example.moneyway.community.service.comment;

import com.example.moneyway.community.dto.request.CreateCommentRequest;
import com.example.moneyway.community.dto.response.CommentResponse;

import java.util.List;
import java.util.Objects;

public interface CommentService {

    Long createComment(Long userId, CreateCommentRequest request);

    void deleteComment(Long commentId, Long userId);

    /**
     * [수정] 'isMine' 필드를 계산하기 위해 현재 조회하는 사용자의 ID(viewerId)를 받습니다.
     * viewerId가 null일 경우 비회원으로 간주합니다.
     */
    List<CommentResponse> getActiveCommentsByPostId(Long postId, Long viewerId);
}