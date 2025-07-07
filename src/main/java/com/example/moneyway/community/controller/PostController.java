package com.example.moneyway.community.controller;

import com.example.moneyway.community.dto.request.CreatePostRequest;
import com.example.moneyway.community.dto.request.PostUpdateRequest;
import com.example.moneyway.community.dto.response.PostDetailResponse;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.service.post.PostService;
import com.example.moneyway.community.service.view.PostViewService;
import com.example.moneyway.community.type.PostSortType;
import com.example.moneyway.user.service.UserService; // [추가] UserService 의존성
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // [추가]

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService; // [추가] 사용자 조회를 위한 서비스
    private final PostViewService postViewService;
    /**
     * ✅ 게시글 생성
     */
    @PostMapping
    public ResponseEntity<Void> createPost(
            // [수정] Spring Security의 UserDetails 객체를 주입받습니다.
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @Valid @RequestBody CreatePostRequest request) {

        // principal에서 이메일을 꺼내 우리 시스템의 User ID를 조회합니다.
        Long userId = userService.findByEmail(principal.getUsername()).getId();
        Long postId = postService.createPost(userId, request);

        // [개선] ServletUriComponentsBuilder를 사용하여 생성된 리소스의 URI를 생성합니다.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(postId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    /**
     * ✅ 게시글 수정
     */
    // [수정] 중복된 경로를 제거합니다.
    @PatchMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        Long userId = userService.findByEmail(principal.getUsername()).getId();
        postService.updatePost(postId, userId, request); // 서비스 파라미터 순서에 맞게 전달
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

        Long userId = userService.findByEmail(principal.getUsername()).getId();
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 게시글 상세 조회 (조회수 증가 포함)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            HttpServletRequest request) {

        Long viewerId = (principal != null) ? userService.findByEmail(principal.getUsername()).getId() : null;
        String ipAddress = request.getRemoteAddr();

        // [수정] 조회수 증가 로직을 PostViewService에 위임
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
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal, // [개선] 개인화된 정보를 위해 추가
            Pageable pageable) {

        Long viewerId = (principal != null) ? userService.findByEmail(principal.getUsername()).getId() : null;
        Page<PostSummaryResponse> page = postService.getPostList(sort, challenge, viewerId, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * ✅ 특정 사용자의 게시글 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostSummaryResponse>> getUserPosts(
            @PathVariable Long userId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) { // [개선] 개인화된 정보를 위해 추가

        Long viewerId = (principal != null) ? userService.findByEmail(principal.getUsername()).getId() : null;
        List<PostSummaryResponse> posts = postService.getUserPosts(userId, viewerId);
        return ResponseEntity.ok(posts);
    }
}