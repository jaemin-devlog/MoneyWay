package com.example.moneyway.community.service.post;

import com.example.moneyway.community.dto.request.CreatePostRequest;
import com.example.moneyway.community.dto.request.UpdatePostRequest;
import com.example.moneyway.community.dto.response.PostDetailResponse;
import com.example.moneyway.community.dto.response.PostSummaryResponse;

import java.util.List;

public interface PostService {

    // 게시글 생성
    Long createPost(Long userId, CreatePostRequest request);

    // 게시글 수정
    void updatePost(Long postId, Long userId, UpdatePostRequest request);

    // 게시글 삭제
    void deletePost(Long postId, Long userId);

    // 단일 게시글 상세 조회
    PostDetailResponse getPostDetail(Long postId, Long viewerId);

    // 게시글 리스트 조회 (카테고리/정렬 조건 등은 추후 확장)
    List<PostSummaryResponse> getPostList();

    // (선택) 사용자 작성한 게시글 리스트 조회
    List<PostSummaryResponse> getUserPosts(Long userId);
}
