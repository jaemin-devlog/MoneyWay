package com.example.moneyway.community.service.like;

public interface PostLikeService {

    /**
     * 게시글에 대한 '좋아요' 상태를 토글(추가/삭제)합니다.
     * @param postId 좋아요를 누를 게시글 ID
     * @param userId 좋아요를 누른 사용자 ID
     * @return 토글 후의 좋아요 상태 (true: 좋아요, false: 좋아요 취소)
     */
    boolean toggleLike(Long postId, Long userId);
}