package com.example.moneyway.community.dto.request;

import com.example.moneyway.community.dto.request.common.BasePostRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 수정 요청 DTO
 *
 * - 작성자가 자신의 게시글을 수정할 때 사용됩니다.
 * - isChallenge은 수정할 수 없습니다.
 */
@Getter
@Setter
public class PostUpdateRequest extends BasePostRequest {

    private Long postId; // 수정 대상 게시글 ID
}
