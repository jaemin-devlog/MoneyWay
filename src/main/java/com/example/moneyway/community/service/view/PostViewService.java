package com.example.moneyway.community.service.view;

public interface PostViewService {

    /**
     * 게시글의 조회수를 증가시킵니다.
     * 동일한 사용자는 일정 시간 내에 중복으로 조회수를 올릴 수 없습니다.
     * @param postId 조회할 게시글 ID
     * @param viewerId 조회하는 사용자 ID (비회원은 null)
     * @param ipAddress 조회하는 사용자의 IP 주소
     */
    void increaseViewCount(Long postId, Long viewerId, String ipAddress);
}