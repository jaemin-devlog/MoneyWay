package com.example.moneyway.community.dto.response;

import com.example.moneyway.community.dto.response.common.WriterInfo; // [추가]
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostDetailResponse {

    // --- 게시글 정보 ---
    private Long postId;
    private String title;
    private String content;
    private Integer totalCost;
    private boolean isChallenge; // [개선] primitive boolean 타입 사용
    private String thumbnailUrl;
    private List<String> imageUrls;
    private LocalDateTime createdAt;

    // --- 작성자 정보 ---
    private WriterInfo writerInfo; // [개선] 공통 DTO 사용

    // --- 통계 정보 ---
    private Integer likeCount;
    private Integer commentCount;
    private Integer scrapCount;
    private Integer viewCount;

    // --- 현재 사용자의 상태 정보 ---
    private boolean isMine;     // 내가 쓴 글인지 여부
    private boolean isLiked;    // 내가 좋아요를 눌렀는지 여부
    private boolean isScrapped; // 내가 스크랩했는지 여부
}