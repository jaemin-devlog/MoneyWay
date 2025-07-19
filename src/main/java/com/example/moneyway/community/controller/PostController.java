package com.example.moneyway.community.controller;

import com.example.moneyway.auth.userdetails.UserDetailsImpl;
import com.example.moneyway.community.dto.request.CreatePostRequest;
import com.example.moneyway.community.dto.request.PostUpdateRequest;
import com.example.moneyway.community.dto.response.PostDetailResponse;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.service.post.PostService;
import com.example.moneyway.community.service.view.PostViewService;
import com.example.moneyway.community.type.PostSortType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final PostViewService postViewService;
    /**
     * ✅ 게시글 생성
     */
    @PostMapping
    public ResponseEntity<Void> createPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CreatePostRequest request) {

        // 인증된 사용자 ID로 게시글을 생성합니다.
        Long postId = postService.createPost(userDetails.getUserId(), request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(postId)
                .toUri();

        return ResponseEntity.created(location).build();
    }


    /**
     * ✅ 게시글 수정
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        postService.updatePost(postId, userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        postService.deletePost(postId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 게시글 상세 조회 (조회수 증가 포함)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletRequest request) {

        Long viewerId = (userDetails != null) ? userDetails.getUserId() : null;
        String ipAddress = request.getRemoteAddr();

        postViewService.increaseViewCount(postId, viewerId, ipAddress);

        PostDetailResponse response = postService.getPostDetail(postId, viewerId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 게시글 목록 조회 (정렬 + 챌린지 필터)
     */
    @GetMapping
    public ResponseEntity<Page<PostSummaryResponse>> getPostList(
            @RequestParam(required = false) PostSortType sort,
            @RequestParam(required = false) Boolean challenge,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {

        Long viewerId = (userDetails != null) ? userDetails.getUserId() : null;
        Page<PostSummaryResponse> page = postService.getPostList(sort, challenge, viewerId, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * ✅ 특정 사용자의 게시글 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostSummaryResponse>> getUserPosts(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long viewerId = (userDetails != null) ? userDetails.getUserId() : null;
        List<PostSummaryResponse> posts = postService.getUserPosts(userId, viewerId);
        return ResponseEntity.ok(posts);
    }
}