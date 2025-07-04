package com.example.moneyway.community.repository.post;

import com.example.moneyway.community.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPostId(Long postId);         // 게시글 이미지 조회
    void deleteAllByPostId(Long postId);               // 게시글 삭제 시 이미지 일괄 삭제
}
