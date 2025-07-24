package com.example.moneyway.community.repository.comment;

import com.example.moneyway.community.domain.Comment;
import com.example.moneyway.community.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query; // @Query 관련 import 제거
// import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.id = :postId AND c.deleted = false ORDER BY c.createdAt ASC")
    List<Comment> findByPostIdWithUser(Long postId);

    int countByPostAndDeletedFalse(Post post);

    void deleteAllByPost(Post post);
}