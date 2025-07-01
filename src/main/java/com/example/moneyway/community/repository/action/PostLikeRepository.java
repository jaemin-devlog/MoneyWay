package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId); // 특정 사용자가 좋아요 눌렀는지 확인

    void deleteByPostIdAndUserId(Long postId, Long userId);    // 좋아요 취소 (삭제)

    int countByPostId(Long postId);                            // 게시글 좋아요 수 조회
}
