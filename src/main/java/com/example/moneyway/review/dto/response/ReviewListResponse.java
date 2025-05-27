package com.example.moneyway.review.dto.response;

import java.time.LocalDateTime;

public class ReviewListResponse {
    private Long id;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;

    public ReviewListResponse(Long id, String content, Long userId, LocalDateTime createdAt) {
    }
    // 댓글, 좋아요 수 등 추가 가능
}
