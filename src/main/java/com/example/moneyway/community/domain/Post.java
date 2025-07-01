package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 커뮤니티 게시글 도메인
 *
 * 여행 후기 또는 챌린지 참여 글을 저장하는 핵심 엔티티입니다.
 * 좋아요, 댓글 수, 스크랩 수 등 집계 데이터를 함께 관리합니다.
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
    private Long id;                          // 게시글 ID

    @Column(nullable = false)
    private Long userId;                      // 작성자 ID

    @Column(nullable = false)
    private String writerNickname = "익명";


    @Column(nullable = false, length = 100)
    private String title;                     // 제목

    @Lob
    @Column(nullable = false)
    private String content;                   // 본문 내용 (긴 글도 가능)

    private Integer totalCost;                // 총 지출 비용 (nullable 허용)

    private Boolean isChallenge = false;      // 챌린지 참여 여부

    private Integer likeCount = 0;            // 좋아요 수

    private Integer commentCount = 0;         // 댓글 수

    private Integer scrapCount = 0;           // 스크랩 수

    private Integer viewCount = 0;            // 조회수

    @Column(nullable = false)
    private String thumbnailUrl;              // 썸네일 이미지 URL (별도 첨부된 이미지)

    private LocalDateTime createdAt;          // 생성일시

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // 저장 시 자동 생성
    }

}
