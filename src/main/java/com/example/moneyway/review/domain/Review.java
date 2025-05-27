package com.example.moneyway.review.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import java.time.LocalDateTime;

@Entity
public class Review {
    @Id @GeneratedValue
    private Long id;

    private String content;

    private Long userId;

    private LocalDateTime createdAt;

    // (좋아요, 댓글 등 연관관계 생략)
}
