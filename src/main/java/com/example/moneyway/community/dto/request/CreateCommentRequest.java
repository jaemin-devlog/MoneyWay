package com.example.moneyway.community.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateCommentRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private final Long postId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private final String content;

    @JsonCreator
    public CreateCommentRequest(
            @JsonProperty("postId") Long postId,
            @JsonProperty("content") String content) {
        this.postId = postId;
        this.content = content;
    }
}