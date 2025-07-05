package com.example.moneyway.community.repository.post;

import com.example.moneyway.community.domain.Post; // [추가] Post 엔티티 임포트
import com.example.moneyway.community.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPost(Post post);

    void deleteAllByPost(Post post);
}