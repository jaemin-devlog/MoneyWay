package com.example.moneyway.community.repository.action;

import com.example.moneyway.community.domain.Post; // [추가]
import com.example.moneyway.community.domain.PostView;
import com.example.moneyway.user.domain.User; // [추가]
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PostViewRepository extends JpaRepository<PostView, Long> {

    boolean existsByPostAndUserAndViewedAtAfter(Post post, User user, LocalDateTime afterTime);

    boolean existsByPostAndIpAddressAndViewedAtAfter(Post post, String ipAddress, LocalDateTime afterTime);

    void deleteAllByPost(Post post);
}