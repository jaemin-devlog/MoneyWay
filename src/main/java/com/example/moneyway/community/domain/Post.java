package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 커뮤니티 게시글 도메인
 *
 * 사용자가 작성한 여행 후기 또는 챌린지 참여 글을 저장하는 핵심 엔티티입니다.
 * 좋아요, 댓글, 조회수, 총 비용 등의 커뮤니티 정렬/집계 정보도 포함됩니다.
 */
@Entity
@Getter
@Setter
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    private Integer totalCost;          // 총 비용

    private Boolean isChallenge = false;

    private Integer likeCount = 0;
    private Integer viewCount = 0;
    private Integer commentCount = 0;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}


