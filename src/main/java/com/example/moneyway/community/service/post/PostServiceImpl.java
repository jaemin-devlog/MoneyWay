package com.example.moneyway.community.service.post;

import com.example.moneyway.common.exception.CustomException.CustomPostException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.domain.PostImage;
import com.example.moneyway.community.dto.request.CreatePostRequestDto;
import com.example.moneyway.community.dto.request.PostUpdateRequest;
import com.example.moneyway.community.dto.response.CommentResponse;
import com.example.moneyway.community.dto.response.PostDetailResponse;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.dto.response.common.WriterInfo;
import com.example.moneyway.community.repository.action.PostLikeRepository;
import com.example.moneyway.community.repository.action.PostScrapRepository;
import com.example.moneyway.community.repository.post.PostImageRepository;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.community.service.comment.CommentService;
import com.example.moneyway.community.type.PostSortType;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB
    private static final int MAX_IMAGE_COUNT = 10;

    @Override
    public Long createPost(Long userId, CreatePostRequestDto request, MultipartFile thumbnail, List<MultipartFile> photos) {
        User user = userService.findActiveUserById(userId);

        validateFile(thumbnail, true);
        if (photos != null) {
            if (photos.size() > MAX_IMAGE_COUNT) {
                throw new CustomPostException(ErrorCode.FILE_TOO_MANY);
            }
            photos.forEach(file -> validateFile(file, false));
        }

        String thumbnailUrl = storeFile(thumbnail);

        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .totalCost(request.getTotalCost())
                .thumbnailUrl(thumbnailUrl)
                .build();
        postRepository.save(post);

        List<String> imageUrls = storeFiles(photos);
        savePostImages(post, imageUrls);

        return post.getId();
    }

    @Override
    public void updatePost(Long postId, Long userId, PostUpdateRequest request, MultipartFile thumbnail, List<MultipartFile> photos) {
        // 1. 게시글 조회 및 권한 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomPostException(ErrorCode.POST_FORBIDDEN_UPDATE);
        }

        // 2. 썸네일 업데이트 로직
        String newThumbnailUrl = post.getThumbnailUrl(); // 기본값: 기존 썸네일 유지
        if (thumbnail != null && !thumbnail.isEmpty()) {
            // 새 썸네일 파일이 있으면: 기존 파일 삭제 후 새 파일 저장
            validateFile(thumbnail, false);
            deletePhysical(post.getThumbnailUrl());
            newThumbnailUrl = storeFile(thumbnail);
        } else if (request.getThumbnailUrl() == null || request.getThumbnailUrl().isBlank()) {
            // 썸네일 삭제 요청 (프론트에서 thumbnailUrl을 null이나 빈 값으로 보낼 때)
            deletePhysical(post.getThumbnailUrl());
            newThumbnailUrl = null;
        }

        // 3. 본문 이미지 업데이트 로직
        // 3.1. 유지할 기존 이미지 URL 목록
        List<String> urlsToKeep = request.getImageUrls() != null ? request.getImageUrls() : Collections.emptyList();

        // 3.2. 삭제할 기존 이미지 찾아서 물리적 파일 삭제
        List<PostImage> imagesToDelete = post.getImages().stream()
                .filter(img -> !urlsToKeep.contains(img.getImageUrl()))
                .toList();

        imagesToDelete.forEach(img -> deletePhysical(img.getImageUrl()));

        // 3.3. 새로운 이미지 파일 저장
        if (photos != null) {
            int currentImageCount = urlsToKeep.size();
            if (currentImageCount + photos.size() > MAX_IMAGE_COUNT) {
                throw new CustomPostException(ErrorCode.FILE_TOO_MANY);
            }
            photos.forEach(file -> validateFile(file, false));
        }
        List<String> newImageUrls = storeFiles(photos);

        // 4. 게시글 엔티티 업데이트 (텍스트, 썸네일)
        post.updatePost(
                request.getTitle(),
                request.getContent(),
                request.getTotalCost(),
                newThumbnailUrl
        );

        // 5. 이미지 연관관계 업데이트 (기존 이미지 삭제, 새 이미지 추가)
        postImageRepository.deleteAll(imagesToDelete); // DB에서 삭제
        post.getImages().removeAll(imagesToDelete);   // 컬렉션에서 삭제

        savePostImages(post, newImageUrls); // 새 이미지 DB에 저장 및 연관관계 설정
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findByIdWithUserAndImages(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new CustomPostException(ErrorCode.POST_FORBIDDEN_DELETE);
        }

        // Delete all physical files associated with the post
        deletePhysical(post.getThumbnailUrl());
        post.getImages().forEach(image -> deletePhysical(image.getImageUrl()));

        postRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId, Long viewerId) {
        Post post = postRepository.findByIdWithUserAndImages(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        List<String> imageUrls = post.getImages().stream()
                .map(PostImage::getImageUrl)
                .toList();

        List<CommentResponse> comments = commentService.getActiveCommentsByPostId(postId, viewerId);

        return toDetailResponse(post, viewerId, imageUrls, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPostList(PostSortType sort, Long viewerId, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), getSort(sort));

        Page<Post> postsPage = postRepository.findAllWithUser(sortedPageable);

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

    private void validateFile(MultipartFile file, boolean isNullable) {
        if (file == null || file.isEmpty()) {
            if (isNullable) {
                return;
            }
            throw new CustomPostException(ErrorCode.FILE_IS_EMPTY);
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new CustomPostException(ErrorCode.FILE_TOO_LARGE);
        }

        String contentType = Optional.ofNullable(file.getContentType()).orElse("");
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new CustomPostException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
    }

    private String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String extension = Optional.ofNullable(file.getOriginalFilename())
                    .filter(name -> name.contains("."))
                    .map(name -> name.substring(name.lastIndexOf(".")))
                    .orElse("");

            String storedFileName = UUID.randomUUID().toString() + extension;
            Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(basePath);

            Path destinationPath = basePath.resolve(storedFileName).normalize();
            if (!destinationPath.startsWith(basePath)) {
                throw new SecurityException("Invalid path traversal attempt");
            }

            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            return publicBaseUrl + "/uploads/" + storedFileName;
        } catch (IOException e) {
            throw new CustomPostException(ErrorCode.FILE_SAVE_FAILED);
        }
    }

    private List<String> storeFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        return files.stream()
                .map(this::storeFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void deletePhysical(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        try {
            String fileName = Paths.get(fileUrl).getFileName().toString();
            Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete physical file: {}", fileUrl, e);
        }
    }

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
                    postImage.setPost(post);
                    return postImage;
                })
                .toList();
        postImageRepository.saveAll(postImages);
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
