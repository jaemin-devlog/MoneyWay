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
        // 게시글 생성
        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .build(); // category 제거됨

        postRepository.save(post);

        // 이미지가 있을 경우, PostImage 리스트 생성 후 저장
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<PostImage> images = request.getImageUrls().stream()
                    .map(url -> PostImage.builder()
                            .postId(post.getId())  // 연관관계 대신 postId 직접 지정
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
