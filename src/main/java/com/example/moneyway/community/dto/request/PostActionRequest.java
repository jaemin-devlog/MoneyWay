package com.example.moneyway.community.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostActionRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private final Long postId;

    @JsonCreator
    public PostActionRequest(
            @JsonProperty("postId") Long postId) {
        this.postId = postId;
    }
}