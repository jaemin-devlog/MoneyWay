package com.example.moneyway.community.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 작성자 요약 정보 DTO
 * 게시글이나 댓글에서 사용자 정보를 포함할 때 사용하는 응답 객체
 */
@Getter
@Builder
public class AuthorDto {
    private Long id;                  // 사용자 ID
    private String nickname;         // 닉네임
    private String profileImageUrl;  // 프로필 이미지 URL
}
