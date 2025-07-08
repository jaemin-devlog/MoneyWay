package com.example.moneyway.user.dto.response;

import com.example.moneyway.community.dto.response.PostSummaryResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class MyPageResponse {
    private final UserResponse userInfo; // 내 정보
    private final Page<PostSummaryResponse> myPosts; // 내가 쓴 글 목록 (첫 페이지)
    private final Page<PostSummaryResponse> myScraps; // 내가 스크랩한 글 목록 (첫 페이지)
}