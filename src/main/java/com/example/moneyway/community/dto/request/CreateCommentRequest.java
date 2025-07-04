package com.example.moneyway.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 댓글 생성 요청 DTO
 *
 * - 특정 게시글(Post)에 달리는 댓글을 생성할 때 사용됩니다.
 * - 대댓글 기능은 제거되었으므로 parentId는 포함되지 않습니다.
 */
@Getter
@Setter
public class CreateCommentRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId; // 댓글이 달릴 게시글 ID

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content; // 댓글 내용
}
