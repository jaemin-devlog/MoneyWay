package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.domain.PostLike;
import com.example.moneyway.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndUser(Post post, User user);

    void deleteByPostAndUser(Post post, User user);

    int countByPost(Post post);

    void deleteAllByPost(Post post);

    Optional<PostLike> findByPostAndUser(Post post, User user);

    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user = :user AND pl.post.id IN :postIds")
    Set<Long> findPostIdsByUserAndPostIdsIn(@Param("user") User user, @Param("postIds") List<Long> postIds);
}