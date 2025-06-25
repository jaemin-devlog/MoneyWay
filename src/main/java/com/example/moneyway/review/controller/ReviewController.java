package com.example.moneyway.review.controller;

import com.example.moneyway.review.domain.Review;
import com.example.moneyway.review.dto.request.ReviewUpdateRequest;
import com.example.moneyway.review.dto.request.ReviewWriteRequest;
import com.example.moneyway.review.dto.response.ReviewListResponse;
import com.example.moneyway.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;
    //생성
    @PostMapping
    public ResponseEntity<Void> writeReview(@RequestBody ReviewWriteRequest request) {
        reviewService.writeReview(request);
        return ResponseEntity.ok().build();
    }
    //전체조회
    @GetMapping
    public ResponseEntity<List<ReviewListResponse>> getReviews() {
        return ResponseEntity.ok(reviewService.getReviews());
    }
    //단일조회
    @GetMapping("/{id}")
    public ResponseEntity<ReviewListResponse> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReview(id));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateReview(@PathVariable Long id,
                                             @RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(id, request);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }
}
