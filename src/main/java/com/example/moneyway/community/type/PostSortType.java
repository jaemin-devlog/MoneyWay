package com.example.moneyway.community.type;

/**
 * 게시글 정렬 기준 열거형
 * - 컨트롤러 요청 시 ?sort= 으로 전달받아 사용
 */
public enum PostSortType {
    LATEST,     // 최신순
    LIKES,      // 좋아요순
    COMMENTS,   // 댓글순
    SCRAPS      // 스크랩순
}
