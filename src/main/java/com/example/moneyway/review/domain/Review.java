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
    @Id @GeneratedValue
    private Long id;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;

    // ★ 여기에 추가!
    private Long planId;
    // or (만약 Plan이라는 엔티티와 연관관계라면)
    // @ManyToOne
    // private Plan plan;
}

