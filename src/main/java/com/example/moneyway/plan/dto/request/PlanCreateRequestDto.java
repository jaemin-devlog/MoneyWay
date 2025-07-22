package com.example.moneyway.plan.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class PlanCreateRequestDto {

    @Schema(description = "여행 계획의 제목", example = "나홀로 제주 미식여행")
    @NotBlank(message = "제목은 비워둘 수 없습니다.")
    private String title;

    @Schema(description = "여행 계획의 총 가격", example = "150000")
    @NotNull(message = "총 가격은 필수입니다.")
    @Min(value = 0, message = "총 가격은 0 이상이어야 합니다.")
    private int totalPrice;

    @Schema(description = "시간표에 배치된 장소 목록")
    @Valid // 리스트 안의 DTO 객체들까지 유효성 검사를 진행합니다.
    @NotEmpty(message = "장소 목록은 비워둘 수 없습니다.")
    private List<PlanPlaceCreateDto> places;
}