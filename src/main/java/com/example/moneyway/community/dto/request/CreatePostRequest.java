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
    private String title;                // 게시글 제목
    private String content;             // 게시글 본문 내용
    private List<String> imageUrls;     // 프론트엔드에서 업로드한 이미지 URL 리스트
    private String thumbnailUrl;        // 썸네일 이미지 URL (imageUrls 중 하나)
}
