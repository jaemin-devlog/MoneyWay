package com.example.moneyway.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하로 작성해주세요.")
    private String title;

    @NotBlank(message = "본문 내용은 필수입니다.")
    @Size(max = 5000, message = "내용은 5000자 이하로 작성해주세요.")
    private String content;

    @PositiveOrZero(message = "지출 비용은 0 이상의 값이어야 합니다.")
    private Integer totalCost;
}
