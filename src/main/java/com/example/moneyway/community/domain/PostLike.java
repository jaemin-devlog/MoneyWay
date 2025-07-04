package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 좋아요 도메인
 *
 * 사용자가 특정 게시글(Post)에 좋아요를 누른 이력을 저장합니다.
 * 불변 객체로 설계하여 데이터의 신뢰성을 보장합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"postId", "userId"})
        }
)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    @Builder
    public PostLike(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }
}