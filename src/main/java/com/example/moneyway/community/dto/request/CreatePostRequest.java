package com.example.moneyway.community.dto.request;

import com.example.moneyway.community.dto.request.common.BasePostRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * 게시글 생성 요청 DTO (불변 객체)
 */
@Getter
public class CreatePostRequest extends BasePostRequest { // BasePostRequest 상속

    @JsonCreator
    public CreatePostRequest(
            @JsonProperty("title") String title,
            @JsonProperty("content") String content,
            @JsonProperty("totalCost") Integer totalCost,
            @JsonProperty("thumbnailUrl") String thumbnailUrl,
            @JsonProperty("imageUrls") List<String> imageUrls) {
        // 부모 클래스의 생성자 호출
        super(title, content, totalCost, thumbnailUrl, imageUrls);
    }
}