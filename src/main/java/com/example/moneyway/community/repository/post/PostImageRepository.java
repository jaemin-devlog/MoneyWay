package com.example.moneyway.community.repository.post;

import com.example.moneyway.community.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPostId(Long postId);       // 게시글 ID로 모든 이미지 조회

    void deleteAllByPostId(Long postId);             // 게시글 ID 기준으로 모든 이미지 삭제
}
