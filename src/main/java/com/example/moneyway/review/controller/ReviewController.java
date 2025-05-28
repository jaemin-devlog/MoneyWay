package com.example.moneyway.review.controller;

import com.example.moneyway.review.domain.Review;
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

    @PostMapping
    public ResponseEntity<Void> writeReview(@RequestBody ReviewWriteRequest request) {
        reviewService.writeReview(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ReviewListResponse>> getReviews() {
        return ResponseEntity.ok(reviewService.getReviews());
    }
}
