package com.example.moneyway.community.service.scrap;

public interface PostScrapService {

    /**
     * 게시글에 대한 '스크랩' 상태를 토글(추가/삭제)합니다.
     * @param postId 스크랩할 게시글 ID
     * @param userId 스크랩한 사용자 ID
     * @return 토글 후의 스크랩 상태 (true: 스크랩, false: 스크랩 취소)
     */
    boolean toggleScrap(Long postId, Long userId);
}