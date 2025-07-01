package com.example.moneyway.community.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 게시글 수정 요청 DTO
 * 기존 게시글을 수정할 때 제목, 본문, 이미지 추가/삭제, 썸네일 변경 등을 전달
 */
@Getter
@Setter
public class UpdatePostRequest {
    private String title;                  // 수정할 게시글 제목
    private String content;               // 수정할 게시글 본문
    private List<String> imageUrls;       // 새로 추가할 이미지 URL 리스트
    private List<Long> deleteImageIds;    // 삭제할 기존 이미지 ID 리스트
    private String thumbnailUrl;          // 새로 설정할 썸네일 URL
}
