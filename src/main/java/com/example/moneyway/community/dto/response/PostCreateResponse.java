package com.example.moneyway.community.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateResponse {
    private Long postId;
    private String message;
}
