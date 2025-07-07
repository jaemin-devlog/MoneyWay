package com.example.moneyway.community.domain;

import com.example.moneyway.common.domain.BaseTimeEntity;
import com.example.moneyway.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
public class Comment extends BaseTimeEntity {

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 필드명을 isDeleted -> deleted 로 변경하여 일관성 유지
    @Column(nullable = false)
    private boolean deleted = false;

    @Builder
    public Comment(Post post, User user, String content) {
        this.post = post;
        this.user = user;
        this.content = content;
    }

    //== 비즈니스 로직 ==//
    public void delete() {
        this.deleted = true;
    }

    // 편의를 위한 작성자 닉네임 조회 메서드
    public String getWriterNickname() {
        return this.user.getNickname();
    }
}