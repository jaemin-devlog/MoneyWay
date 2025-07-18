package com.example.moneyway.plan.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PlanPlaceCreateDto {

    @Schema(description = "일정에 추가할 장바구니 항목의 ID", example = "102")
    @NotNull(message = "장바구니 항목 ID는 필수입니다.")
    private Long cartId;

    @Schema(description = "여행 일차 (1, 2, 3, 4)", example = "1")
    @NotNull(message = "일차 정보는 필수입니다.")
    private Integer dayNumber;

    @Schema(description = "방문 시작 시간", example = "2024-08-10T10:00:00")
    @NotNull(message = "방문 시작 시간은 필수입니다.")
    private LocalDateTime startTime;

    @Schema(description = "방문 종료 시간", example = "2024-08-10T12:30:00")
    @NotNull(message = "방문 종료 시간은 필수입니다.")
    private LocalDateTime endTime;
}