package com.example.moneyway.plan.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanUpdateRequestDto {
    @NotBlank(message = "계획 제목은 필수입니다.")
    private String title;

    @NotNull(message = "총 가격은 필수입니다.")
    @Min(value = 0, message = "총 가격은 0 이상이어야 합니다.")
    private int totalPrice;

    @NotEmpty(message = "계획에 포함될 장소는 최소 하나 이상이어야 합니다.")
    @Valid
    private List<PlanPlaceUpdateRequestDto> places;
}
