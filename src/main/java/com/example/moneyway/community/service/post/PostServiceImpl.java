package com.example.moneyway.community.service.post;

import com.example.moneyway.common.exception.CustomException.CustomPostException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.community.domain.*;
import com.example.moneyway.community.dto.request.CreatePostRequest;
import com.example.moneyway.community.dto.request.PostUpdateRequest;
import com.example.moneyway.community.dto.response.PostDetailResponse;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.repository.action.*;
import com.example.moneyway.community.repository.comment.CommentRepository;
import com.example.moneyway.community.repository.post.PostImageRepository;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.community.type.PostSortType;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional // Apply transactionality at the class level for cleaner code
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostViewRepository postViewRepository;
    private final UserService userService;

    @Override
    public Long createPost(Long userId, CreatePostRequest request) {
        String writerNickname = userService.getNicknameById(userId);
        if (writerNickname == null || writerNickname.isBlank()) {
            throw new CustomPostException(ErrorCode.USER_NICKNAME_NOT_FOUND);
        }

        Post post = Post.builder()
                .userId(userId)
                .writerNickname(writerNickname)
                .title(request.getTitle())
                .content(request.getContent())
                .totalCost(request.getTotalCost())
                .isChallenge(request.getIsChallenge())
                .thumbnailUrl(request.getThumbnailUrl())
                .build();
        postRepository.save(post);

        savePostImages(post.getId(), request.getImageUrls());

        return post.getId();
    }

    @Override
    public void updatePost(Long postId, Long userId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new CustomPostException(ErrorCode.POST_FORBIDDEN_UPDATE);
        }

        post.updatePost(
                request.getTitle(),
                request.getContent(),
                request.getTotalCost(),
                request.getThumbnailUrl()
        );

        // Update images by deleting old ones and adding new ones
        postImageRepository.deleteAllByPostId(postId);
        savePostImages(postId, request.getImageUrls());
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUserId().equals(userId)) {
            throw new CustomPostException(ErrorCode.POST_FORBIDDEN_UPDATE);
        }

        // Delete all related data to maintain data integrity
        postImageRepository.deleteAllByPostId(postId);
        commentRepository.deleteAllByPostId(postId);
        postLikeRepository.deleteAllByPostId(postId);
        postScrapRepository.deleteAllByPostId(postId);
        postViewRepository.deleteAllByPostId(postId);

        postRepository.delete(post);
    }

    @Override
    public void increaseViewCount(Long postId, Long viewerId, String ipAddress) {
        // [IMPROVEMENT] Ensure the post exists before performing any write operations.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        boolean alreadyViewed = (viewerId != null)
                ? postViewRepository.existsByPostIdAndUserIdAndViewedAtAfter(postId, viewerId, oneHourAgo)
                : postViewRepository.existsByPostIdAndIpAddressAndViewedAtAfter(postId, ipAddress, oneHourAgo);

        if (!alreadyViewed) {
            postViewRepository.save(PostView.builder()
                    .postId(postId)
                    .userId(viewerId)
                    .ipAddress(ipAddress)
                    .build());
            post.increaseViewCount();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId, Long viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        List<String> imageUrls = postImageRepository.findByPostId(postId).stream()
                .map(PostImage::getImageUrl)
                .toList();

        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .totalCost(post.getTotalCost())
                .isChallenge(post.getIsChallenge())
                .thumbnailUrl(post.getThumbnailUrl())
                .imageUrls(imageUrls)
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .scrapCount(post.getScrapCount())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .writerNickname(post.getWriterNickname())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPostList(PostSortType sort, Boolean challenge, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                getSort(sort)
        );

        Page<Post> posts = (challenge != null && challenge)
                ? postRepository.findByIsChallenge(true, sortedPageable)
                : postRepository.findAll(sortedPageable);

        // [IMPROVEMENT] Use the helper method to map DTOs.
        return posts.map(this::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostSummaryResponse> getUserPosts(Long userId) {
        // [IMPROVEMENT] Use a more specific repository method that returns a List.
        List<Post> userPosts = postRepository.findByUserId(userId);

        // [IMPROVEMENT] Use the helper method to map DTOs.
        return userPosts.stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    // ========================= Private Helper Methods =========================

    private void savePostImages(Long postId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        List<PostImage> postImages = imageUrls.stream()
                .map(url -> PostImage.builder()
                        .postId(postId)
                        .imageUrl(url)
                        .build())
                .toList();
        postImageRepository.saveAll(postImages);
    }

    private Sort getSort(PostSortType type) {
        if (type == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (type) {
            case LIKES -> Sort.by(Sort.Direction.DESC, "likeCount", "createdAt");
            case COMMENTS -> Sort.by(Sort.Direction.DESC, "commentCount", "createdAt");
            case SCRAPS -> Sort.by(Sort.Direction.DESC, "scrapCount", "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    /**
     * [NEW] A helper method to map a Post entity to a PostSummaryResponse DTO.
     * This reduces code duplication and centralizes the mapping logic.
     */
    private PostSummaryResponse toSummaryResponse(Post post) {
        return PostSummaryResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .thumbnailUrl(post.getThumbnailUrl())
                .isChallenge(post.getIsChallenge())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .scrapCount(post.getScrapCount())
                .writerNickname(post.getWriterNickname())
                .createdAt(post.getCreatedAt())
                .build();
    }
}