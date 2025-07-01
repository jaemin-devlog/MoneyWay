package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.*;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게시글 ID

    @Column(nullable = false)
    private Long userId; // 게시글 작성자 ID

    @Column(nullable = false, length = 100)
    private String title; // 제목

    @Lob
    @Column(nullable = false)
    private String content; // 본문 내용

    private Integer totalCost; // 총 지출 비용 (예산 기반)

    private Boolean isChallenge = false; // 챌린지 참여 여부

    private Integer likeCount = 0; // 좋아요 수

    private Integer viewCount = 0; // 조회수

    private Integer commentCount = 0; // 댓글 수

    private LocalDateTime createdAt; // 생성 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
