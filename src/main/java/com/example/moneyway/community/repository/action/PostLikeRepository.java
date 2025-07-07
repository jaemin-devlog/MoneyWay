package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.domain.PostLike;
import com.example.moneyway.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // ✅ [추가] 특정 게시글에 특정 사용자가 '좋아요'를 눌렀는지 여부를 확인합니다.
    boolean existsByPostAndUser(Post post, User user);

    boolean existsByPostAndUser_Id(Post post, Long userId);

    void deleteByPostAndUser(Post post, User user);

    int countByPost(Post post);

    void deleteAllByPost(Post post);

    Optional<PostLike> findByPostAndUser(Post post, User user);
}