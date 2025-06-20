package com.example.moneyway.review.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    private Long reviewId;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;
}
