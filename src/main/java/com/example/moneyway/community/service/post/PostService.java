package com.example.moneyway.community.service.post;

import com.example.moneyway.community.dto.request.CreatePostRequest;
import com.example.moneyway.community.dto.request.PostUpdateRequest;
import com.example.moneyway.community.dto.response.PostDetailResponse;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.type.PostSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    // 게시글 생성
    Long createPost(Long userId, CreatePostRequest request);

    // 게시글 수정
    void updatePost(Long postId, Long userId, PostUpdateRequest request);

    // 게시글 삭제
    void deletePost(Long postId, Long userId);

    // 게시글 상세 조회
    PostDetailResponse getPostDetail(Long postId, Long viewerId);

    Page<PostSummaryResponse> getPostList(PostSortType sort, Boolean challenge, Long viewerId, Pageable pageable);

    List<PostSummaryResponse> getUserPosts(Long userId, Long viewerId);

}