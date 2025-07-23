package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // [추가]

@Entity
@Getter
@Setter // [추가]
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_image")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Post 객체와 직접 연관
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, columnDefinition = "TEXT") // ✅ columnDefinition = "TEXT" 추가
    private String imageUrl;

    @Builder
    public PostImage(Post post, String imageUrl) {
        this.post = post;
        this.imageUrl = imageUrl;
    }

    // [추가] 양방향 연관관계 설정을 위한 편의 메서드
    public void setPost(Post post) {
        this.post = post;
    }
}