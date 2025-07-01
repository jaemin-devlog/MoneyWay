package com.example.moneyway.community.repository.comment;

import com.example.moneyway.community.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdAndIsDeletedFalse(Long postId);//게시글 ID로 삭제되지 않은 댓글 목록 조회

    int countByPostIdAndIsDeletedFalse(Long postId); //게시글 ID로 삭제되지 않은 댓글 개수 조회
}
