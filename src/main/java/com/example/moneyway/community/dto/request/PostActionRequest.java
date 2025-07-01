package com.example.moneyway.community.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글에 대한 사용자 액션 요청 DTO
 *
 * - 좋아요, 스크랩 등의 단순 액션에 사용됩니다.
 * - postId만 전달되며, 사용자 정보는 인증 토큰에서 추출합니다.
 */
@Getter
@Setter
public class PostActionRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId; // 대상 게시글 ID
}
