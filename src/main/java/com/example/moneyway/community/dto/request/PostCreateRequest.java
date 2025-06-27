package com.example.moneyway.community.dto.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PostCreateRequest {
    private Long planId;
    private Long userId;
    private String content;
    private Integer total_cost;     // int → Integer
}
