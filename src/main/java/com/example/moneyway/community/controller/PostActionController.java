package com.example.moneyway.community.controller;

import com.example.moneyway.community.service.like.PostLikeService;
import com.example.moneyway.community.service.scrap.PostScrapService;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostActionController {

    private final PostLikeService postLikeService;
    private final PostScrapService postScrapService;
    private final UserService userService;

    /**
     * 게시글 좋아요 토글 API
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal User principal) {

        Long userId = userService.findByEmail(principal.getUsername()).getId();
        boolean isLiked = postLikeService.toggleLike(postId, userId);

        // 클라이언트가 상태를 바로 알 수 있도록 boolean 값을 반환
        return ResponseEntity.ok(Map.of("isLiked", isLiked));
    }

    /**
     * 게시글 스크랩 토글 API
     */
    @PostMapping("/{postId}/scrap")
    public ResponseEntity<Map<String, Boolean>> toggleScrap(
            @PathVariable Long postId,
            @AuthenticationPrincipal User principal) {

        Long userId = userService.findByEmail(principal.getUsername()).getId();
        boolean isScrapped = postScrapService.toggleScrap(postId, userId);

        return ResponseEntity.ok(Map.of("isScrapped", isScrapped));
    }
}