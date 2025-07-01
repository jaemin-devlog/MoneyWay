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
        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .build();

        postRepository.save(post);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<PostImage> images = request.getImageUrls().stream()
                    .map(url -> PostImage.builder()
                            .post(post)
                            .imageUrl(url)
                            .build())
                    .toList();

            postImageRepository.saveAll(images);
        }

        return post.getId();
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
