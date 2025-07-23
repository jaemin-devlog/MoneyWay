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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
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

        // 기존 이미지를 모두 삭제하고 새로 저장
        post.getImages().clear();
        savePostImages(post, request.getImageUrls());
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomPostException(ErrorCode.POST_FORBIDDEN_UPDATE);
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId, Long viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        List<String> imageUrls = post.getImages().stream()
                .map(PostImage::getImageUrl)
                .toList();

        List<CommentResponse> comments = commentService.getActiveCommentsByPostId(postId, viewerId);

        return toDetailResponse(post, viewerId, imageUrls, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPostList(PostSortType sort, Boolean challenge, Long viewerId, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), getSort(sort));

        Page<Post> postsPage = (challenge != null && challenge)
                ? postRepository.findByIsChallenge(true, sortedPageable)
                : postRepository.findAll(sortedPageable);

        List<PostSummaryResponse> summaryResponses = convertPostsToSummaryResponse(postsPage.getContent(), viewerId);

        return new PageImpl<>(summaryResponses, pageable, postsPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostSummaryResponse> getUserPosts(Long userId, Long viewerId) {
        User user = userService.findActiveUserById(userId);
        List<Post> userPosts = postRepository.findByUser(user);
        return convertPostsToSummaryResponse(userPosts, viewerId);
    }

    // ========================= Private Helper Methods =========================

    private List<PostSummaryResponse> convertPostsToSummaryResponse(List<Post> posts, Long viewerId) {
        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> likedPostIds = Collections.emptySet();
        Set<Long> scrappedPostIds = Collections.emptySet();

        if (viewerId != null) {
            User viewer = userService.findActiveUserById(viewerId);
            List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
            likedPostIds = postLikeRepository.findPostIdsByUserAndPostIdsIn(viewer, postIds);
            scrappedPostIds = postScrapRepository.findPostIdsByUserAndPostIdsIn(viewer, postIds);
        }

        final Set<Long> finalLikedPostIds = likedPostIds;
        final Set<Long> finalScrappedPostIds = scrappedPostIds;

        return posts.stream()
                .map(post -> toSummaryResponse(post, finalLikedPostIds, finalScrappedPostIds))
                .collect(Collectors.toList());
    }

    private void savePostImages(Post post, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        List<PostImage> postImages = imageUrls.stream()
                .map(url -> {
                    PostImage postImage = PostImage.builder().imageUrl(url).build();
                    postImage.setPost(post); // 연관관계 설정
                    return postImage;
                })
                .toList();
        post.getImages().addAll(postImages);
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

    private PostSummaryResponse toSummaryResponse(Post post, Set<Long> likedPostIds, Set<Long> scrappedPostIds) {
        boolean isLiked = likedPostIds.contains(post.getId());
        boolean isScrapped = scrappedPostIds.contains(post.getId());

        String summaryContent = post.getContent();
        if (summaryContent != null && summaryContent.length() > 100) {
            summaryContent = summaryContent.substring(0, 100);
        }

        return PostSummaryResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(summaryContent)
                .thumbnailUrl(post.getThumbnailUrl())
                .totalCost(post.getTotalCost())
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
