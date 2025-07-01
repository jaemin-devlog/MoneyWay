package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostViewRepository extends JpaRepository<PostView, Long> {

    Optional<PostView> findByPostIdAndUserId(Long postId, Long userId);              // 로그인 유저의 조회 기록 조회

    Optional<PostView> findByPostIdAndIpAddress(Long postId, String ipAddress);      // 비로그인 유저(IP) 조회 기록 조회

    int countByPostId(Long postId);                                                  // 게시글 총 조회수

    int countByUserId(Long userId);                                                  // 특정 유저가 조회한 게시글 수

    boolean existsByPostIdAndUserIdAndViewedAtAfter(Long postId, Long userId, LocalDateTime after);
    // 일정 시간 이내에 조회했는지 확인 (중복 조회 방지용)
}
