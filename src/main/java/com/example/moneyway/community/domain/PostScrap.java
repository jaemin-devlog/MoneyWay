package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 스크랩(PostScrap) 도메인 클래스
 *
 * 사용자가 게시글(Post)을 스크랩하여 마이페이지 또는 개인 북마크 공간에 저장한 이력을 나타냅니다.
 * 한 사용자가 같은 게시글을 여러 번 스크랩하지 못하도록 postId + userId에 유니크 제약을 둡니다.
 */
@Entity
@Getter
@Setter
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
}

