package com.example.moneyway.community.controller;

import com.example.moneyway.community.dto.request.CreatePostRequest;
import com.example.moneyway.community.dto.request.PostUpdateRequest;
import com.example.moneyway.community.dto.response.PostDetailResponse;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.service.post.PostService;
import com.example.moneyway.community.type.PostSortType;
import com.example.moneyway.user.domain.User; // User 도메인 import
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // @AuthenticationPrincipal import
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    /**
     * ✅ 게시글 생성
     * [개선] @AuthenticationPrincipal을 사용하여 인증된 사용자 정보를 안전하게 가져옵니다.
     *       헤더를 직접 파싱하는 방식보다 안전하고 테스트가 용이합니다.
     * [개선] 생성 성공 시, 200 OK 대신 201 Created 상태 코드와 함께 생성된 리소스의 URI를 반환합니다.
     */
    @PostMapping
    public ResponseEntity<Void> createPost(@AuthenticationPrincipal User user,
                                           @Valid @RequestBody CreatePostRequest request) {
        Long postId = postService.createPost(user.getId(), request);
        return ResponseEntity.created(URI.create("/api/posts/" + postId)).build();
    }

    /**
     * ✅ 게시글 수정
     * [개선] @AuthenticationPrincipal을 사용하여 작성자 본인 여부를 확인합니다.
     * [개선] 리소스의 일부만 수정하므로, PUT 대신 PATCH 메서드를 사용합니다.
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal User user,
                                           @Valid @RequestBody PostUpdateRequest request) {
        postService.updatePost(postId, user.getId(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 게시글 삭제
     * [개선] @AuthenticationPrincipal을 사용하여 작성자 본인 여부를 확인합니다.
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal User user) {
        postService.deletePost(postId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 게시글 상세 조회 (조회수 증가 포함)
     * [개선] @AuthenticationPrincipal을 사용하여 로그인/비로그인 사용자를 구분합니다.
     *       - 로그인 사용자: user 객체가 주입됨 (user != null)
     *       - 비로그인 사용자: user 객체가 null임 (user == null)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId,
                                                            @AuthenticationPrincipal User user,
                                                            HttpServletRequest request) {
        // viewerId는 인증된 사용자의 ID이며, 비로그인 시에는 null이 됩니다.
        Long viewerId = (user != null) ? user.getId() : null;
        String ipAddress = request.getRemoteAddr();

        // 조회수 증가는 비동기 처리 또는 AOP로 분리하면 더욱 좋습니다.
        postService.increaseViewCount(postId, viewerId, ipAddress);

        PostDetailResponse response = postService.getPostDetail(postId, viewerId);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 게시글 목록 조회 (정렬 + 챌린지 필터)
     * 이 API는 인증이 필요 없으므로 변경사항이 없습니다.
     */
    @GetMapping
    public ResponseEntity<Page<PostSummaryResponse>> getPostList(@RequestParam(required = false) PostSortType sort,
                                                                 @RequestParam(required = false) Boolean challenge,
                                                                 Pageable pageable) {
        Page<PostSummaryResponse> page = postService.getPostList(sort, challenge, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * ✅ 특정 사용자의 게시글 목록 조회
     * 이 API는 인증이 필요 없으므로 변경사항이 없습니다.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostSummaryResponse>> getUserPosts(@PathVariable Long userId) {
        List<PostSummaryResponse> posts = postService.getUserPosts(userId);
        return ResponseEntity.ok(posts);
    }
}