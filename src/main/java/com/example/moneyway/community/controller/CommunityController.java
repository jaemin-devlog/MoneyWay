package com.example.moneyway.community.controller;

import com.example.moneyway.community.dto.request.PostUpdateRequest;
import com.example.moneyway.community.dto.request.PostCreateRequest;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/reviews")
public class CommunityController {
    @Autowired
    private CommunityService communityService;
    //생성
    @PostMapping
    public ResponseEntity<Void> writeReview(@RequestBody PostCreateRequest request) {
        communityService.writeReview(request);
        return ResponseEntity.ok().build();
    }
    //전체조회
    @GetMapping
    public ResponseEntity<List<PostSummaryResponse>> getReviews() {
        return ResponseEntity.ok(communityService.getReviews());
    }
    //단일조회
    @GetMapping("/{id}")
    public ResponseEntity<PostSummaryResponse> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getReview(id));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateReview(@PathVariable Long id,
                                             @RequestBody PostUpdateRequest request) {
        communityService.updateReview(id, request);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        communityService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
}
