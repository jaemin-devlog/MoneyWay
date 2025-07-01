package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.*;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 ID

    @Column(nullable = false)
    private Long postId; // 연결된 게시글 ID

    @Column(nullable = false)
    private Long userId; // 댓글 작성자 ID

    @Lob
    @Column(nullable = false)
    private String content; // 댓글 내용

    private LocalDateTime createdAt; // 생성 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}


