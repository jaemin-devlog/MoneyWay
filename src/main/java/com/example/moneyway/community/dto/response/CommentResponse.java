package com.example.moneyway.community.dto.response;

import com.example.moneyway.community.dto.response.common.WriterInfo; // [추가]
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long commentId;
    private String content;
    private LocalDateTime createdAt;

    // --- 작성자 정보 ---
    private WriterInfo writerInfo; // [개선] 공통 DTO 사용

    // --- 현재 사용자의 상태 정보 ---
    private boolean isMine; // 내가 쓴 댓글인지 여부
}