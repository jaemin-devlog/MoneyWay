package com.example.moneyway.community.repository;

import com.example.moneyway.community.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}