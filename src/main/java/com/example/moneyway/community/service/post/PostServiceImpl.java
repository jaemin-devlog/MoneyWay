package com.example.moneyway.community.service.post;

import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.domain.PostImage;
import com.example.moneyway.community.dto.request.CreatePostRequest;
import com.example.moneyway.community.dto.request.UpdatePostRequest;
import com.example.moneyway.community.dto.response.PostDetailResponse;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.community.repository.post.PostImageRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

    /**
     * 게시글 생성
     */
    @Override
    public Long createPost(Long userId, CreatePostRequest request) {
        // 게시글 객체 생성 (썸네일 URL 포함)
        Post post = Post.builder()
                .userId(userId)                              // 작성자 ID
                .title(request.getTitle())                   // 제목
                .content(request.getContent())               // 본문
                .totalCost(request.getTotalCost())           // 총 비용
                .isChallenge(request.getIsChallenge())       // 챌린지 여부
                .thumbnailUrl(request.getThumbnailUrl())     // 썸네일 URL
                .build();

        postRepository.save(post); // 게시글 저장 → postId 생성됨

        // 이미지 URL 리스트가 있을 경우
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            String thumbnailUrl = request.getThumbnailUrl(); // 썸네일 URL 받아오기

            List<PostImage> images = request.getImageUrls().stream()
                    .distinct() // 중복 제거 (이미 DB에 unique 설정했지만 안전하게)
                    .map(url -> PostImage.builder()
                            .postId(post.getId())                  // 연결된 게시글 ID
                            .imageUrl(url)                         // 이미지 URL
                            .isThumbnail(url.equals(thumbnailUrl))// 썸네일이면 true
                            .build())
                    .toList();

            postImageRepository.saveAll(images); // 이미지 일괄 저장
        }

        return post.getId(); // 생성된 게시글 ID 반환
    }



    // 나머지 메서드는 아래에서 점진적으로 구현 예정
    @Override
    public void updatePost(Long postId, Long userId, UpdatePostRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PostDetailResponse getPostDetail(Long postId, Long viewerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<PostSummaryResponse> getPostList() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<PostSummaryResponse> getUserPosts(Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
