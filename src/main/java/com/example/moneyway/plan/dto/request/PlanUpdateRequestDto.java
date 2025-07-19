package com.example.moneyway.plan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanUpdateRequestDto {

    @NotBlank(message = "계획 제목은 필수입니다.")
    private String title;

    @NotNull
    private List<PlanPlaceUpdateRequestDto> places;
}
