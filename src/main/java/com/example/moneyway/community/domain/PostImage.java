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
@Table(name = "post_image", uniqueConstraints = @UniqueConstraint(columnNames = {"postId", "imageUrl"}))
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String imageUrl;
}
