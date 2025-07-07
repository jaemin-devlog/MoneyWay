package com.example.moneyway.community.dto.request;

import com.example.moneyway.community.dto.request.common.BasePostRequest;
import lombok.Getter;
// [제거] import lombok.Setter;

import java.util.List;

/**
 * 게시글 수정 요청 DTO (불변 객체 및 구조 개선)
 * - 수정 대상 postId는 URL 경로에서 받으므로 DTO에서 제거합니다.
 */
@Getter
// [제거] @Setter
public class PostUpdateRequest extends BasePostRequest {
    // [제거] private Long postId;

    // [개선] 생성자를 통해 필드 주입
    public PostUpdateRequest(String title, String content, Integer totalCost, String thumbnailUrl, List<String> imageUrls) {
        this.title = title;
        this.content = content;
        this.totalCost = totalCost;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrls = imageUrls;
    }
}