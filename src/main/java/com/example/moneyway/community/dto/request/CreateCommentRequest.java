package com.example.moneyway.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor; // [추가]

@Getter
@RequiredArgsConstructor // [개선] final 필드를 위한 생성자 자동 생성
public class CreateCommentRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private final Long postId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private final String content;
}