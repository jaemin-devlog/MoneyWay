package com.example.moneyway.community.service.post;

import com.example.moneyway.common.exception.CustomException.CustomPostException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.community.domain.*;
import com.example.moneyway.community.dto.request.CreatePostRequest;
import com.example.moneyway.community.dto.request.PostUpdateRequest;
import com.example.moneyway.community.dto.response.CommentResponse;
import com.example.moneyway.community.dto.response.PostDetailResponse;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.dto.response.common.WriterInfo;
import com.example.moneyway.community.repository.action.*;
import com.example.moneyway.community.repository.comment.CommentRepository;
import com.example.moneyway.community.repository.post.PostImageRepository;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.community.service.comment.CommentService;
import com.example.moneyway.community.type.PostSortType;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostViewRepository postViewRepository;
    private final UserService userService;
    private final CommentService commentService;

    @Override
    public Long createPost(Long userId, CreatePostRequest request) {
        User user = userService.findActiveUserById(userId);

        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .totalCost(request.getTotalCost())
                .isChallenge(request.isChallenge())
                .thumbnailUrl(request.getThumbnailUrl())
                .build();
        postRepository.save(post);

        savePostImages(post, request.getImageUrls());

        return post.getId();
    }

    @Override
    public void updatePost(Long postId, Long userId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomPostException(ErrorCode.POST_FORBIDDEN_UPDATE);
        }

        post.updatePost(
                request.getTitle(),
                request.getContent(),
                request.getTotalCost(),
                request.getThumbnailUrl()
        );

        postImageRepository.deleteAllByPost(post);
        savePostImages(post, request.getImageUrls());
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomPostException(ErrorCode.POST_FORBIDDEN_UPDATE);
        }

        postImageRepository.deleteAllByPost(post);
        commentRepository.deleteAllByPost(post);
        postLikeRepository.deleteAllByPost(post);
        postScrapRepository.deleteAllByPost(post);
        postViewRepository.deleteAllByPost(post);

        postRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId, Long viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        List<String> imageUrls = postImageRepository.findByPost(post).stream()
                .map(PostImage::getImageUrl)
                .toList();

        List<CommentResponse> comments = commentService.getActiveCommentsByPostId(postId, viewerId);

        return toDetailResponse(post, viewerId, imageUrls, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPostList(PostSortType sort, Boolean challenge, Long viewerId, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), getSort(sort));

        Page<Post> posts = (challenge != null && challenge)
                ? postRepository.findByIsChallenge(true, sortedPageable)
                : postRepository.findAll(sortedPageable);

        return posts.map(post -> toSummaryResponse(post, viewerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostSummaryResponse> getUserPosts(Long userId, Long viewerId) {
        User user = userService.findActiveUserById(userId);
        List<Post> userPosts = postRepository.findByUser(user);

        return userPosts.stream()
                .map(post -> toSummaryResponse(post, viewerId))
                .toList();
    }

    // ========================= Private Helper Methods =========================

    private void savePostImages(Post post, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        List<PostImage> postImages = imageUrls.stream()
                .map(url -> PostImage.builder()
                        .post(post)
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

    private PostDetailResponse toDetailResponse(Post post, Long viewerId, List<String> imageUrls, List<CommentResponse> comments) {
        boolean isLiked = false;
        boolean isScrapped = false;

        if (viewerId != null) {
            User viewerUser = userService.findActiveUserById(viewerId);
            isLiked = postLikeRepository.existsByPostAndUser(post, viewerUser);
            isScrapped = postScrapRepository.existsByPostAndUser(post, viewerUser);
        }

        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .totalCost(post.getTotalCost())
                .isChallenge(post.isChallenge())
                .thumbnailUrl(post.getThumbnailUrl())
                .imageUrls(imageUrls)
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .scrapCount(post.getScrapCount())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .writerInfo(WriterInfo.from(post.getUser()))
                .isMine(Objects.equals(post.getUser().getId(), viewerId))
                .isLiked(isLiked)
                .isScrapped(isScrapped)
                .comments(comments)
                .build();
    }

    private PostSummaryResponse toSummaryResponse(Post post, Long viewerId) {
        boolean isLiked = false;
        boolean isScrapped = false;

        if (viewerId != null) {
            User viewerUser = userService.findActiveUserById(viewerId);
            isLiked = postLikeRepository.existsByPostAndUser(post, viewerUser);
            isScrapped = postScrapRepository.existsByPostAndUser(post, viewerUser);
        }

        return PostSummaryResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .thumbnailUrl(post.getThumbnailUrl())
                .isChallenge(post.isChallenge())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .scrapCount(post.getScrapCount())
                .writerInfo(WriterInfo.from(post.getUser()))
                .createdAt(post.getCreatedAt())
                .isLiked(isLiked)
                .isScrapped(isScrapped)
                .build();
    }
}
