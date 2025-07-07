package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.Post; // [추가]
import com.example.moneyway.community.domain.PostScrap;
import com.example.moneyway.user.domain.User; // [추가]
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {

    boolean existsByPostAndUser_Id(Post post, Long userId);

    void deleteByPostAndUser(Post post, User user);

    List<PostScrap> findAllByUser(User user);

    int countByPost(Post post);

    void deleteAllByPost(Post post);

    Optional<PostScrap> findByPostAndUser(Post post, User user);
}