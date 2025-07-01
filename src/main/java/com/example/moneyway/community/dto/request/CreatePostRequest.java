package com.example.moneyway.community.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 게시글 생성 요청 DTO
 * 클라이언트가 제목, 본문, 이미지 URL, 썸네일 정보를 포함해 게시글을 작성할 때 사용
 */
@Getter
@Setter
public class CreatePostRequest {
    private String title;           // 게시글 제목
    private String content;         // 게시글 내용
    private Integer totalCost;      // 총 지출 비용
    private Boolean isChallenge;    // 챌린지 여부
    private String thumbnailUrl;    // 썸네일 이미지 URL
    private List<String> imageUrls; // 본문에 첨부된 이미지들
}
