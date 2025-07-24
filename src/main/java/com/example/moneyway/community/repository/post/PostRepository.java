package com.example.moneyway.community.repository.post;

import com.example.moneyway.community.domain.Post;
import com.example.moneyway.user.domain.User; // [추가] User 엔티티 임포트
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.user LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Post> findByIdWithUserAndImages(@Param("id") Long id);

    Optional<Post> findById(Long id);

    // 챌린지 여부로 찾는 것
    Page<Post> findByIsChallenge(Boolean isChallenge, Pageable pageable);

    // User 객체 자체를 받아 페이징 처리된 게시글을 조회합니다.
    Page<Post> findByUser(User user, Pageable pageable);

    // User 객체 자체를 받아 해당 사용자의 모든 게시글을 조회합니다.
    List<Post> findByUser(User user);
}