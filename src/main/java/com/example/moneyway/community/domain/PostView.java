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
 * 불변 객체로 설계하여 데이터의 신뢰성을 보장합니다.
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
    private Long id;

    @Column(nullable = false)
    private Long postId;

    // 비회원 조회도 기록할 수 있으므로 nullable
    private Long userId;

    // IP 기반 조회를 위해 nullable
    private String ipAddress;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    @Builder
    public PostView(Long postId, Long userId, String ipAddress) {
        this.postId = postId;
        this.userId = userId;
        this.ipAddress = ipAddress;
    }
}
