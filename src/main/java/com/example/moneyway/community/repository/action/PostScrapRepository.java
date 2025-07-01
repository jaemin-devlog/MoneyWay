package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.PostScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId); // 사용자가 스크랩했는지 여부 확인

    void deleteByPostIdAndUserId(Long postId, Long userId);    // 스크랩 취소

    List<PostScrap> findAllByUserId(Long userId);              // 사용자의 모든 스크랩 게시글 조회
}
