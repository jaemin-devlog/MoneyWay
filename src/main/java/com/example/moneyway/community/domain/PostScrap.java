package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 스크랩(PostScrap) 도메인
 *
 * 사용자가 게시글을 스크랩한 이력을 나타냅니다.
 * 불변 객체로 설계하여 데이터의 신뢰성을 보장합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_scrap",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"postId", "userId"})},
        indexes = {@Index(columnList = "userId")}
)
public class PostScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public PostScrap(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }
}