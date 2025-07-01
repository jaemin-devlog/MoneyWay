package com.example.moneyway.community.repository.comment;

import com.example.moneyway.community.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);         // 특정 게시글에 달린 모든 댓글 조회

    List<Comment> findByUserId(Long userId);         // 특정 사용자가 작성한 모든 댓글 조회

    List<Comment> findByParentId(Long parentId);     // 대댓글(답글) 조회용

    int countByPostId(Long postId);                  // 게시글의 총 댓글 수 계산
}
