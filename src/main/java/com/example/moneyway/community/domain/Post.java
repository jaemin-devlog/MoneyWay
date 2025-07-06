package com.example.moneyway.community.domain;

import com.example.moneyway.common.domain.BaseTimeEntity; // createdAt, updatedAt 자동 관리를 위해 상속
import com.example.moneyway.user.domain.User; // User 엔티티 임포트
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
public class Post extends BaseTimeEntity { // BaseTimeEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //User user (직접적인 객체 연관)
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

    // 카운트 필드들은 성능을 위해 비정규화된 데이터로 유지하는 것이 좋음
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

    @Builder
    public Post(User user, String title, String content, Integer totalCost, Boolean isChallenge, String thumbnailUrl) {
        this.user = user; // User 객체 할당
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.totalCost = totalCost != null ? totalCost : 0;
        this.isChallenge = isChallenge != null ? isChallenge : false;
    }

    //== 비즈니스 로직 ==//

    public void updatePost(String title, String content, Integer totalCost, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.totalCost = totalCost;
        this.thumbnailUrl = thumbnailUrl;
    }

    // [추가] 편의를 위한 작성자 닉네임 조회 메서드
    public String getWriterNickname() {
        return this.user.getNickname();
    }

    // ... (카운트 증가/감소 관련 비즈니스 메서드들은 그대로 유지) ...
    public void increaseViewCount() { this.viewCount++; }
    public void increaseCommentCount() { this.commentCount++; }
    public void decreaseCommentCount() { this.commentCount = Math.max(0, this.commentCount - 1); }
    public void increaseLikeCount() { this.likeCount++; }
    public void decreaseLikeCount() { this.likeCount = Math.max(0, this.likeCount - 1); }
    public void increaseScrapCount() { this.scrapCount++; }
    public void decreaseScrapCount() { this.scrapCount = Math.max(0, this.scrapCount - 1); }
}