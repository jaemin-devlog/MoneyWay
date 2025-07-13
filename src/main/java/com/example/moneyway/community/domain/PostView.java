package com.example.moneyway.community.domain;

import com.example.moneyway.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post_view",
        indexes = {
                @Index(name = "idx_post_user", columnList = "post_id, user_id"),
                @Index(name = "idx_post_ip", columnList = "post_id, ipAddress")
        }
)
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String ipAddress;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    @Builder
    public PostView(Post post, User user, String ipAddress) {
        this.post = post;
        this.user = user;
        this.ipAddress = ipAddress;
    }
}