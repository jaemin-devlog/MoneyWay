package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 게시글 조회 기록(PostView) 도메인
 *
 * 사용자가 특정 게시글을 조회한 이력을 저장하여 중복 조회수 방지에 사용됩니다.
 * 비회원은 IP로, 회원은 userId로 기록됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post_view",
        indexes = {
                @Index(name = "idx_post_user", columnList = "postId, userId"),
                @Index(name = "idx_post_ip", columnList = "postId, ipAddress")
        }
)
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(nullable = false)
    private Long postId; // 게시글 ID

    @Column
    private Long userId; // 회원일 경우 사용자 ID (nullable)

    @Column
    private String ipAddress; // 비회원일 경우 IP 주소 기반 (nullable)

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt; // 조회 시각

    @Builder
    public PostView(Long postId, Long userId, String ipAddress) {
        this.postId = postId;
        this.userId = userId;
        this.ipAddress = ipAddress;
    }
}
