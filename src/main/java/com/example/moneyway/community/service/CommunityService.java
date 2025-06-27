package com.example.moneyway.community.service;

import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.dto.request.PostUpdateRequest;
import com.example.moneyway.community.dto.request.PostCreateRequest;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommunityService {
    private final PostRepository postRepository;

    public CommunityService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void writeReview(PostCreateRequest request) {
        Post post = new Post();
        post.setPlanId(request.getPlanId());
        post.setUserId(request.getUserId());
        post.setContent(request.getContent());
        post.setTotalCost(request.getTotal_cost());
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
    }
    public List<PostSummaryResponse> getReviews() {
        List<Post> posts = postRepository.findAll();
        List<PostSummaryResponse> responseList = new ArrayList<>();
        for (Post post : posts) {
            PostSummaryResponse response = new PostSummaryResponse();
            response.setId(post.getId());
            response.setPlanId(post.getPlanId());
            response.setUserId(post.getUserId());
            response.setContent(post.getContent());
            response.setTotalCost(post.getTotalCost());
            response.setCreatedAt(post.getCreatedAt());
            responseList.add(response);
        }
        return responseList;
    }
    // 단일 조회
    public PostSummaryResponse getReview(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 없음"));
        return toResponse(post);
    }

    // 수정
    public void updateReview(Long id, PostUpdateRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰 없음"));
        post.setContent(request.getContent());
        post.setTotalCost(request.getTotalCost());
        postRepository.save(post);
    }

    // 삭제
    public void deleteReview(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 리뷰 없음");
        }
        postRepository.deleteById(id);
    }

    // 엔티티 → DTO 변환 메서드 (공용)
    private PostSummaryResponse toResponse(Post post) {
        PostSummaryResponse dto = new PostSummaryResponse();
        dto.setId(post.getId());
        dto.setPlanId(post.getPlanId());
        dto.setUserId(post.getUserId());
        dto.setContent(post.getContent());
        dto.setTotalCost(post.getTotalCost());
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }
}