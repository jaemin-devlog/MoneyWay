package com.example.moneyway.community.dto.request;

import com.example.moneyway.community.dto.request.common.BasePostRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
// [제거] import lombok.Setter;

/**
 * 게시글 생성 요청 DTO (불변 객체로 개선)
 */
@Getter
@Setter
@NoArgsConstructor
public class CreatePostRequest extends BasePostRequest {

    private Boolean isChallenge;

    public CreatePostRequest(String title, String content, Integer totalCost, String thumbnailUrl, List<String> imageUrls, Boolean isChallenge) {
        this.title = title;
        this.content = content;
        this.totalCost = totalCost;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrls = imageUrls;
        this.isChallenge = isChallenge;
    }
}