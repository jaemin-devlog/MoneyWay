package com.example.moneyway.community.repository.post;

import com.example.moneyway.community.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserId(Long userId); // 사용자가 작성한 모든 게시글 조회

    boolean existsByUserId(Long userId);     // 사용자가 작성한 게시글 존재 여부 확인
}
