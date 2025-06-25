package com.example.moneyway.review.dto.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReviewWriteRequest {
    private Long plan_id;
    private Long user_id;
    private String content;
    private Integer total_cost;     // int → Integer
}
