package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);   // 좋아요 여부 확인
    void deleteByPostIdAndUserId(Long postId, Long userId);      // 좋아요 취소
    int countByPostId(Long postId);                              // 좋아요 수
    void deleteAllByPostId(Long postId);

}
