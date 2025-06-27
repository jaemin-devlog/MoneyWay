package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 게시글 댓글 도메인
 *
 * 게시글(Post)에 달리는 댓글을 저장합니다.
 * 계층형 구조를 지원하기 위해 parentId를 사용해 대댓글을 구성합니다.
 */

@Entity
@Getter
@Setter
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    @Lob
    @Column(nullable = false)
    private String content;

    private Long parentId; // nullable 대댓글

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

