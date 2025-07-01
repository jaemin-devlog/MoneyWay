package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.PostScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);   // 스크랩 여부 확인
    void deleteByPostIdAndUserId(Long postId, Long userId);      // 스크랩 취소
    List<PostScrap> findAllByUserId(Long userId);                // 사용자 스크랩 목록
    int countByPostId(Long postId);
}
