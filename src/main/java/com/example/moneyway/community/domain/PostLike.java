package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 좋아요 도메인
 *
 * 사용자가 특정 게시글(Post)에 좋아요를 누른 이력을 저장합니다.
 * 한 사용자당 하나의 게시글에 한 번만 좋아요를 누를 수 있도록 유니크 제약을 권장합니다.
 */
@Entity
@Getter
@Setter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"postId", "userId"})
})
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 ID

    private Long postId; // 대상 게시글 ID

    private Long userId; // 좋아요 누른 사용자 ID
}
