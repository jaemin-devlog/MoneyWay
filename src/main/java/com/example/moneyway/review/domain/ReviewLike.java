package com.example.moneyway.review.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


@Entity
public class ReviewLike {
    @Id
    @GeneratedValue
    private Long id;

    private Long reviewId;
    private Long userId;
}
