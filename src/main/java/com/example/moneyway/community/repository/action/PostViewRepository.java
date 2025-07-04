package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostViewRepository extends JpaRepository<PostView, Long> {

    Optional<PostView> findByPostIdAndUserId(Long postId, Long userId);          // 로그인 사용자 조회 기록
    Optional<PostView> findByPostIdAndIpAddress(Long postId, String ipAddress);  // 비로그인 사용자 조회 기록
    int countByPostId(Long postId);                                              // 조회수 계산
    boolean existsByPostIdAndUserIdAndViewedAtAfter(Long postId, Long userId, LocalDateTime afterTime);

    boolean existsByPostIdAndIpAddressAndViewedAtAfter(Long postId, String ipAddress, LocalDateTime afterTime);

    void deleteAllByPostId(Long postId);

}
