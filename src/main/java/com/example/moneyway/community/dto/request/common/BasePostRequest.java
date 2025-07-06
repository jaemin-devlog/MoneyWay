package com.example.moneyway.community.dto.request.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

/**
 * 게시글 요청 공통 필드
 * Create/Update 요청 DTO에서 공통으로 사용됨
 */
@Getter
public abstract class BasePostRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이하로 작성해주세요.")
    protected String title; // 게시글 제목

    @NotBlank(message = "본문 내용은 필수입니다.")
    protected String content; // 게시글 본문

    @PositiveOrZero(message = "지출 비용은 0 이상의 값이어야 합니다.")
    protected Integer totalCost; // 총 지출 비용

    @NotBlank(message = "썸네일 이미지는 필수입니다.")
    protected String thumbnailUrl; // 대표 썸네일 URL

    @Size(max = 10, message = "첨부 이미지는 최대 10개까지만 등록할 수 있습니다.")
    protected List<String> imageUrls; // 첨부 이미지 리스트
}
