package com.example.moneyway.community.controller;

import com.example.moneyway.auth.userdetails.UserDetailsImpl;
import com.example.moneyway.community.dto.request.CreateCommentRequest;
import com.example.moneyway.community.dto.response.CommentResponse;
import com.example.moneyway.community.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 API
     */
    @PostMapping
    public ResponseEntity<Void> createComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        // 인증된 사용자 ID로 댓글을 생성합니다.
        Long commentId = commentService.createComment(userDetails.getUserId(), request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(commentId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    /**
     * 댓글 삭제 API
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId
    ) {
        // 권한 확인을 위해 사용자 ID를 함께 전달합니다.
        commentService.deleteComment(commentId, userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ 댓글 목록 조회 API
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long viewerId = (userDetails != null) ? userDetails.getUserId() : null;
        List<CommentResponse> comments = commentService.getActiveCommentsByPostId(postId, viewerId);
        return ResponseEntity.ok(comments);
    }
}