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
 * 게시글 댓글 도메인
 *
 * 게시글(Post)에 달리는 댓글을 저장합니다.
 * Setter를 제거하고 명확한 비즈니스 메서드를 통해 상태를 관리하여 안정성을 높였습니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // [개선] JPA Auditing 기능 활성화
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    // [개선] @Lob 제거, 일반 텍스트로 충분
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // [개선] writerNickname은 반드시 존재해야 하므로 nullable=false 적용
    @Column(nullable = false)
    private String writerNickname;

    // [개선] primitive boolean 타입으로 변경하여 null 상태 방지
    @Column(nullable = false)
    private boolean isDeleted;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * [개선] @Builder를 생성자에 적용하여 필드 기본값을 보장합니다.
     * 이 생성자는 댓글 생성에 필요한 최소한의 데이터를 받아 안전하게 객체를 생성합니다.
     */
    @Builder
    public Comment(Long postId, Long userId, String content, String writerNickname) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.writerNickname = writerNickname;
        this.isDeleted = false; // isDeleted의 초기값을 false로 명시적으로 보장
    }

    //== 비즈니스 로직 (상태 변경 메서드) ==//

    /**
     * [개선] 댓글을 삭제 상태로 변경하는 비즈니스 메서드.
     * 서비스 레이어에서 직접 isDeleted 필드를 조작하는 것을 방지하고,
     * Comment 객체가 스스로의 상태를 책임지도록 합니다.
     */
    public void delete() {
        this.isDeleted = true;
    }
}