package com.example.moneyway.review.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateRequest {
    private String content;
    private Integer totalCost;
}
