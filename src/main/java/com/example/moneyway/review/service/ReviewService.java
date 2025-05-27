package com.example.moneyway.review.service;

import com.example.moneyway.review.domain.Review;
import com.example.moneyway.review.dto.request.ReviewWriteRequest;
import com.example.moneyway.review.dto.response.ReviewListResponse;
import com.example.moneyway.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Review saveReview(ReviewWriteRequest request) {
        Review review = new Review();
        review.setContent(request.getContent());
        review.setUserId(request.getUserId());
        review.setCreatedAt(LocalDateTime.now());
        review.setPlanId(request.getPlanId()); // ★ 이거 추가
        return reviewRepository.save(review);
    }

    public List<ReviewListResponse> getReviews() {
        return reviewRepository.findAll().stream()
                .map(review -> new ReviewListResponse(
                        review.getId(),
                        review.getContent(),
                        review.getUserId(),
                        review.getCreatedAt()
                )).collect(Collectors.toList());
    }
}
