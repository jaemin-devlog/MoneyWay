package com.example.moneyway.community.dto.request.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

/**
 * 게시글 생성 및 수정 시 공통으로 사용되는 필드를 정의한 추상 클래스입니다.
 * - 제목, 내용, 총 지출, 썸네일 이미지, 첨부 이미지 목록을 포함합니다.
 */
@Getter
public abstract class BasePostRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 20, message = "제목은 20자 이하로 작성해주세요.")
    protected String title; // 게시글 제목

    @NotBlank(message = "본문 내용은 필수입니다.")
    protected String content; // 게시글 내용

    protected Integer totalCost; // 지출 비용 (nullable)

    @NotBlank(message = "썸네일 이미지는 필수입니다.")
    protected String thumbnailUrl; // 썸네일 이미지 URL

    @Size(max = 10, message = "첨부 이미지는 최대 10개까지만 등록할 수 있습니다.")
    protected List<String> imageUrls;
}
