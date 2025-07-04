package com.example.moneyway.community.dto.request;

import com.example.moneyway.community.dto.request.common.BasePostRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 생성 요청 DTO
 *
 * - 사용자가 새 게시글을 생성할 때 필요한 데이터를 담습니다.
 * - 챌린지 여부는 생성 시에만 설정 가능합니다.
 */
@Getter
@Setter
public class CreatePostRequest extends BasePostRequest {

    @NotNull(message = "챌린지 여부는 필수입니다.")
    private Boolean isChallenge; // 챌린지 참여 여부 (true/false)
}
