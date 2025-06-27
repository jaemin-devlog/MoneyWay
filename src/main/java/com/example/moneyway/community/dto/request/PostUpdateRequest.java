package com.example.moneyway.community.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateRequest {
    private String content;
    private Integer totalCost;
}
