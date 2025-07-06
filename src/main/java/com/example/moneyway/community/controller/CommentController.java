package com.example.moneyway.community.controller;

import com.example.moneyway.community.dto.request.CreateCommentRequest;
import com.example.moneyway.community.service.comment.CommentService;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService; // principal(email)로부터 User ID를 얻기 위해 필요

    /**
     * 댓글 생성 API
     */
    @PostMapping
    public ResponseEntity<Void> createComment(
            // Spring Security를 통해 현재 로그인한 사용자의 정보를 가져옵니다.
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        // 1. principal에서 사용자 이메일(username)을 가져와 우리 시스템의 User 객체를 조회합니다.
        User user = userService.findByEmail(principal.getUsername());

        // 2. 조회한 사용자의 ID와 요청 데이터를 서비스에 전달합니다.
        Long commentId = commentService.createComment(user.getId(), request);

        // 3. 생성된 댓글의 위치를 Location 헤더에 담아 201 Created 응답을 반환합니다.
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
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @PathVariable Long commentId
    ) {
        // 댓글을 삭제할 권한이 있는지 확인하기 위해 현재 로그인한 사용자의 ID가 필요합니다.
        User user = userService.findByEmail(principal.getUsername());
        commentService.deleteComment(commentId, user.getId());

        // 성공적으로 삭제되었음을 의미하는 204 No Content 응답을 반환합니다.
        return ResponseEntity.noContent().build();
    }
}