package com.example.moneyway.community.repository.post;

import com.example.moneyway.community.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByIsChallenge(Boolean isChallenge, Pageable pageable); // 챌린지 여부 필터링 + 정렬

    Page<Post> findByUserId(Long userId, Pageable pageable); // 사용자가 작성한 게시글 조회
}
