package com.example.moneyway.community.domain;

import com.example.moneyway.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_scrap",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "user_id"})}
)
public class PostScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Post 객체와 직접 연관
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // User 객체와 직접 연관
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public PostScrap(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}