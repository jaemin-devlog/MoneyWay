package com.example.moneyway.community.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 게시글 이미지 도메인
 *
 * 프론트엔드에서 생성하거나 업로드한 이미지의 URL을
 * 그대로 저장합니다. 외부 저장소(S3 등)는 사용하지 않습니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "post_image")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 이미지 ID

    @Column(nullable = false)
    private Long postId; // 연결된 게시글 ID

    @Column(nullable = false)
    private String imageUrl; // 이미지 URL 경로

    private Boolean isThumbnail = false; // 대표 이미지 여부 (기본 false)
}
