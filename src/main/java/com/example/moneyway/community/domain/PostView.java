package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 게시글 조회 기록(PostView) 도메인 클래스
 *
 * 사용자가 특정 게시글(Post)을 조회한 이력을 저장하는 엔티티입니다.
 * 중복 조회수를 방지하기 위해 사용되며, 사용자 ID 또는 IP 주소를 기준으로 판단합니다.
 *
 * 예를 들어, 같은 사용자가 같은 글을 여러 번 읽더라도
 * 일정 시간(예: 1시간) 이내에는 조회수에 반영되지 않도록 제어합니다.
 *
 * 이 엔티티를 통해 Post의 실제 viewCount 필드를 안전하게 증가시킬 수 있습니다.
 */
@Entity
@Getter
@Setter
@Table(name = "post_view",
        indexes = {
                @Index(name = "idx_post_user", columnList = "postId, userId"),
                @Index(name = "idx_post_ip", columnList = "postId, ipAddress")
        }
)
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    private Long userId;      // 로그인 유저용

    private String ipAddress; // 비로그인 유저용

    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreate() {
        this.viewedAt = LocalDateTime.now();
    }
}
