package com.example.moneyway.community.repository.post;

import com.example.moneyway.community.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // List import 추가

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByIsChallenge(Boolean isChallenge, Pageable pageable);

    Page<Post> findByUserId(Long userId, Pageable pageable);

    // [추가] 페이징 없이 특정 사용자의 모든 게시글을 조회하는 메서드
    List<Post> findByUserId(Long userId);
}