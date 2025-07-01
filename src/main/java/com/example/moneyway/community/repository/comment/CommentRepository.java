package com.example.moneyway.community.repository.comment;

import com.example.moneyway.community.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);           // 게시글 댓글 조회
    int countByPostId(Long postId);                    // 댓글 수 계산
}
