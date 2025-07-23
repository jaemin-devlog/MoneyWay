package com.example.moneyway.community.domain;

import com.example.moneyway.common.domain.BaseTimeEntity;
import com.example.moneyway.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer totalCost;

    @Column(nullable = false)
    private boolean isChallenge;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @Column(nullable = false)
    private int scrapCount = 0;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostScrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostView> views = new ArrayList<>();

    @Builder
    public Post(User user, String title, String content, Integer totalCost, Boolean isChallenge, String thumbnailUrl) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.totalCost = totalCost != null ? totalCost : 0;
        this.isChallenge = isChallenge != null ? isChallenge : false;
    }

    public void updatePost(String title, String content, Integer totalCost, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.totalCost = totalCost;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getWriterNickname() {
        return this.user.getNickname();
    }

    public void increaseViewCount() { this.viewCount++; }
    public void increaseCommentCount() { this.commentCount++; }
    public void decreaseCommentCount() { this.commentCount = Math.max(0, this.commentCount - 1); }
    public void increaseLikeCount() { this.likeCount++; }
    public void decreaseLikeCount() { this.likeCount = Math.max(0, this.likeCount - 1); }
    public void increaseScrapCount() { this.scrapCount++; }
    public void decreaseScrapCount() { this.scrapCount = Math.max(0, this.scrapCount - 1); }
}
