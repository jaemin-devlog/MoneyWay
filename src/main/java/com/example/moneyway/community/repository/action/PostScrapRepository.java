package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.domain.PostScrap;
import com.example.moneyway.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {

    // ✅ [추가] 특정 게시글을 특정 사용자가 '스크랩'했는지 여부를 확인합니다.
    boolean existsByPostAndUser(Post post, User user);

    boolean existsByPostAndUser_Id(Post post, Long userId);

    void deleteByPostAndUser(Post post, User user);

    @Query("SELECT ps FROM PostScrap ps JOIN FETCH ps.post p JOIN FETCH p.user WHERE ps.user = :user ORDER BY ps.id DESC")
    Page<PostScrap> findAllByUserWithPaging(@Param("user") User user, Pageable pageable);

    List<PostScrap> findAllByUser(User user);

    int countByPost(Post post);

    void deleteAllByPost(Post post);

    Optional<PostScrap> findByPostAndUser(Post post, User user);
}