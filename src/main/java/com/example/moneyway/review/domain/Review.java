package com.example.moneyway.review.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue
    private Long id;
    private Long planId;
    private Long userId;
    private String content;
    private Integer totalCost;      // int → Integer
    private LocalDateTime createdAt;
}