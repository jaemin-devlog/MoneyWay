package com.example.moneyway.community.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

/**
 * 게시글 수정 요청 DTO (불변 객체)
 */
@Getter
public class PostUpdateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이하로 작성해주세요.")
    private final String title; // 게시글 제목

    @NotBlank(message = "본문 내용은 필수입니다.")
    private final String content; // 게시글 본문

    @NotNull(message = "지출 비용은 필수입니다.")
    @PositiveOrZero(message = "지출 비용은 0 이상의 값이어야 합니다.")
    private final Integer totalCost; // 총 지출 비용

    @NotBlank(message = "썸네일 이미지는 필수입니다.")
    private final String thumbnailUrl; // 대표 썸네일 URL

    @Size(max = 10, message = "첨부 이미지는 최대 10개까지만 등록할 수 있습니다.")
    private final List<String> imageUrls; // 첨부 이미지 리스트

    @JsonCreator
    public PostUpdateRequest(
            @JsonProperty("title") String title,
            @JsonProperty("content") String content,
            @JsonProperty("totalCost") Integer totalCost,
            @JsonProperty("thumbnailUrl") String thumbnailUrl,
            @JsonProperty("imageUrls") List<String> imageUrls) {
        this.title = title;
        this.content = content;
        this.totalCost = totalCost;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrls = imageUrls;
    }
}
