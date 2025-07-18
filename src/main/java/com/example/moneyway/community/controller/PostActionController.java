package com.example.moneyway.community.controller;

import com.example.moneyway.auth.userdetails.UserDetailsImpl;
import com.example.moneyway.community.service.like.PostLikeService;
import com.example.moneyway.community.service.scrap.PostScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /**
     * 게시글 좋아요 토글 API
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        boolean isLiked = postLikeService.toggleLike(postId, userDetails.getUserId());

        return ResponseEntity.ok(Map.of("isLiked", isLiked));
    }

    /**
     * 게시글 스크랩 토글 API
     */
    @PostMapping("/{postId}/scrap")
    public ResponseEntity<Map<String, Boolean>> toggleScrap(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        boolean isScrapped = postScrapService.toggleScrap(postId, userDetails.getUserId());

        return ResponseEntity.ok(Map.of("isScrapped", isScrapped));
    }
}