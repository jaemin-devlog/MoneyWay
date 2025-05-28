package com.example.moneyway.review.service;

import com.example.moneyway.review.domain.Review;
import com.example.moneyway.review.dto.request.ReviewUpdateRequest;
import com.example.moneyway.review.dto.request.ReviewWriteRequest;
import com.example.moneyway.review.dto.response.ReviewListResponse;
import com.example.moneyway.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void writeReview(ReviewWriteRequest request) {
        Review review = new Review();
        review.setPlanId(request.getPlan_id());
        review.setUserId(request.getUser_id());
        review.setContent(request.getContent());
        review.setTotalCost(request.getTotal_cost());
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }
    public List<ReviewListResponse> getReviews() {
        List<Review> reviews = reviewRepository.findAll();
        List<ReviewListResponse> responseList = new ArrayList<>();
        for (Review review : reviews) {
            ReviewListResponse response = new ReviewListResponse();
            response.setId(review.getId());
            response.setPlanId(review.getPlanId());
            response.setUserId(review.getUserId());
            response.setContent(review.getContent());
            response.setTotalCost(review.getTotalCost());
            response.setCreatedAt(review.getCreatedAt());
            responseList.add(response);
        }
        return responseList;
    }
    // 단일 조회
    public ReviewListResponse getReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 없음"));
        return toResponse(review);
    }

    // 수정
    public void updateReview(Long id, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 없음"));
        review.setContent(request.getContent());
        review.setTotalCost(request.getTotalCost());
        reviewRepository.save(review);
    }

    // 삭제
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 리뷰 없음");
        }
        reviewRepository.deleteById(id);
    }

    // 엔티티 → DTO 변환 메서드 (공용)
    private ReviewListResponse toResponse(Review review) {
        ReviewListResponse dto = new ReviewListResponse();
        dto.setId(review.getId());
        dto.setPlanId(review.getPlanId());
        dto.setUserId(review.getUserId());
        dto.setContent(review.getContent());
        dto.setTotalCost(review.getTotalCost());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}