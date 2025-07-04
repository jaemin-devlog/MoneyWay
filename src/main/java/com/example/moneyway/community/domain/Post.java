package com.example.moneyway.community.domain;

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
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String writerNickname;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // [반영] nullable=false 제약조건 추가
    @Column(nullable = false)
    private Integer totalCost;

    // [반영] primitive type 'boolean'으로 변경하여 null 가능성 원천 차단
    @Column(nullable = false)
    private boolean isChallenge;

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private Integer commentCount;

    @Column(nullable = false)
    private Integer scrapCount;

    @Column(nullable = false)
    private Integer viewCount;

    @Column(nullable = false)
    private String thumbnailUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Post(Long userId, String writerNickname, String title, String content,
                Integer totalCost, Boolean isChallenge, String thumbnailUrl) {
        this.userId = userId;
        this.writerNickname = writerNickname;
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        // [반영] totalCost가 null일 경우 0으로 초기화하여 제약조건 위반 방지
        this.totalCost = totalCost != null ? totalCost : 0;
        // [반영] isChallenge가 null일 경우 false로 초기화
        this.isChallenge = isChallenge != null ? isChallenge : false;
        // count 필드들의 초기값을 0으로 보장
        this.likeCount = 0;
        this.commentCount = 0;
        this.scrapCount = 0;
        this.viewCount = 0;
    }

    //== 비즈니스 로직 (상태 변경 메서드) ==//

    public void updatePost(String title, String content, Integer totalCost, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.totalCost = totalCost;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        this.commentCount = Math.max(0, this.commentCount - 1);
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void increaseScrapCount() {
        this.scrapCount++;
    }

    public void decreaseScrapCount() {
        this.scrapCount = Math.max(0, this.scrapCount - 1);
    }
}