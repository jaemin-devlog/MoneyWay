package com.example.moneyway.community.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostSummaryResponse {
    private Long id;
    private Long planId;
    private Long userId;
    private String content;
    private Integer totalCost;
    private LocalDateTime createdAt;
}