package com.example.moneyway.community.repository.post;

import com.example.moneyway.community.domain.Post;
import com.example.moneyway.user.domain.User; // [추가] User 엔티티 임포트
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import com.example.moneyway.user.domain.User; // [추가] User 엔티티 임포트
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 상세조회용 (N+1 문제 해결: User, Images)
    @Query("SELECT p FROM Post p JOIN FETCH p.user LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Post> findByIdWithUserAndImages(@Param("id") Long id);

    // 목록조회용 (N+1 문제 해결: User)
    @Query(value = "SELECT p FROM Post p JOIN FETCH p.user",
           countQuery = "SELECT count(p) FROM Post p")
    Page<Post> findAllWithUser(Pageable pageable);

    // 챌린지 게시글 목록조회용 (N+1 문제 해결: User)
    @Query(value = "SELECT p FROM Post p JOIN FETCH p.user WHERE p.isChallenge = :isChallenge",
           countQuery = "SELECT count(p) FROM Post p WHERE p.isChallenge = :isChallenge")
    Page<Post> findByIsChallengeWithUser(@Param("isChallenge") Boolean isChallenge, Pageable pageable);

    Optional<Post> findById(Long id);

    Page<Post> findByIsChallenge(Boolean isChallenge, Pageable pageable);

    Page<Post> findByUser(User user, Pageable pageable);

    List<Post> findByUser(User user);
}