package com.example.moneyway.community.dto.request;

import com.example.moneyway.community.dto.request.common.BasePostRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;
// [제거] import lombok.Setter;

/**
 * 게시글 생성 요청 DTO (불변 객체로 개선)
 */
@Getter
// [제거] @Setter
public class CreatePostRequest extends BasePostRequest {

    // [개선] final 키워드 추가 고려 가능 (상속 구조에서는 생략 가능)
    private final Boolean isChallenge;

    // [개선] 생성자를 통해 필드 주입
    public CreatePostRequest(String title, String content, Integer totalCost, String thumbnailUrl, List<String> imageUrls, Boolean isChallenge) {
        this.title = title;
        this.content = content;
        this.totalCost = totalCost;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrls = imageUrls;
        this.isChallenge = isChallenge;
    }
}