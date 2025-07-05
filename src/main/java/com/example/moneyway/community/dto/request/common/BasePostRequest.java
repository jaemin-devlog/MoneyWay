package com.example.moneyway.community.dto.request.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero; // [추가]
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class BasePostRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이하로 작성해주세요.")
    protected String title;

    @NotBlank(message = "본문 내용은 필수입니다.")
    protected String content;

    // [개선] 비용은 0 또는 양수여야 합니다.
    @PositiveOrZero(message = "지출 비용은 0 이상의 값이어야 합니다.")
    protected Integer totalCost;

    @NotBlank(message = "썸네일 이미지는 필수입니다.")
    protected String thumbnailUrl;

    @Size(max = 10, message = "첨부 이미지는 최대 10개까지만 등록할 수 있습니다.")
    protected List<String> imageUrls;
}