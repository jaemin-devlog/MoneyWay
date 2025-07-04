package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
// @Setter는 불변성을 위해 제거하고, 빌더를 통해 생성하는 것을 권장합니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "post_view",
        indexes = {
                @Index(name = "idx_post_user", columnList = "postId, userId"),
                @Index(name = "idx_post_ip", columnList = "postId, ipAddress")
        }
)
// JPA Auditing을 사용해 생성 시각을 자동으로 기록할 수 있습니다.
@EntityListeners(AuditingEntityListener.class)
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    private Long userId;

    private String ipAddress;

    // 조회 시각을 기록하기 위한 필드
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;
}
