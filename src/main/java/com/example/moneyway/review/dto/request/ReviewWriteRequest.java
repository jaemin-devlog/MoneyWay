package com.example.moneyway.review.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewWriteRequest {
    private String content;
    private Long userId;
    private Long planId; // ★ 추가
}
