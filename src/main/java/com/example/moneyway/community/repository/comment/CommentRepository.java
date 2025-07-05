package com.example.moneyway.community.repository.comment;

import com.example.moneyway.community.domain.Comment;
import com.example.moneyway.community.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query; // @Query 관련 import 제거
// import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * [수정] Spring Data JPA의 메서드 이름 규칙을 사용하여 쿼리를 자동 생성합니다.
     * - findBy: 조회 쿼리임을 나타냅니다.
     * - Post_Id: 연관된 Post 엔티티의 id 필드를 조건으로 합니다.
     * - And: 다른 조건을 연결합니다.
     * - DeletedFalse: deleted 필드가 false인 것을 조건으로 합니다.
     * - OrderByCreatedAtAsc: createdAt 필드를 기준으로 오름차순 정렬합니다.
     */
    List<Comment> findByPost_IdAndDeletedFalseOrderByCreatedAtAsc(Long postId);

    int countByPostAndDeletedFalse(Post post);

    void deleteAllByPost(Post post);
}