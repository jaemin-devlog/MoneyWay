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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostViewRepository postViewRepository;
    private final UserService userService;

    @Transactional
    @Override
    public Long createPost(Long userId, CreatePostRequest request) {
        // 사용자 ID로 닉네임 조회 (회원 테이블에서 가져옴)
        String writerNickname = userService.getNicknameById(userId);

        // 닉네임이 비어있거나 null이면 예외 발생
        if (writerNickname == null || writerNickname.isBlank()) {
            throw new CustomPostException(ErrorCode.USER_NICKNAME_NOT_FOUND);
        }

        // Post 엔티티 생성 (작성자, 제목, 본문, 썸네일, 챌린지 여부, 비용 포함)
        Post post = Post.builder()
                .userId(userId)                              // 작성자 ID
                .writerNickname(writerNickname)              // 작성자 닉네임
                .title(request.getTitle())                   // 게시글 제목
                .content(request.getContent())               // 게시글 본문
                .totalCost(request.getTotalCost())           // 총 지출 비용 (nullable)
                .isChallenge(request.getIsChallenge())       // 챌린지 여부
                .thumbnailUrl(request.getThumbnailUrl())     // 썸네일 이미지 URL
                .build();

        // 게시글 저장 (ID 자동 생성)
        postRepository.save(post);

        // 이미지 URL 목록이 비어있지 않으면 PostImage 리스트 생성 및 저장
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<PostImage> postImages = request.getImageUrls().stream()
                    .map(url -> PostImage.builder()
                            .postId(post.getId())            // 연관된 게시글 ID
                            .imageUrl(url)                   // 이미지 URL
                            .build())
                    .toList();

            // 이미지 일괄 저장
            postImageRepository.saveAll(postImages);
        }

        // 생성된 게시글 ID 반환
        return post.getId();
    }

    @Transactional
    @Override
    public void updatePost(Long postId, Long userId, PostUpdateRequest request) {
        // 1. 게시글 ID로 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        // 2. 게시글의 작성자와 현재 사용자가 일치하지 않으면 예외
        if (!post.getUserId().equals(userId)) {
            throw new CustomPostException(ErrorCode.POST_FORBIDDEN_UPDATE);
        }

        // 3. 게시글 필드 수정
        post.setTitle(request.getTitle());                // 제목 변경
        post.setContent(request.getContent());            // 내용 변경
        post.setTotalCost(request.getTotalCost());        // 지출 비용 변경
        post.setThumbnailUrl(request.getThumbnailUrl());  // 썸네일 변경

        postRepository.save(post); // 변경 사항 저장

        // 4. 기존 이미지 삭제
        postImageRepository.deleteAllByPostId(postId);

        // 5. 새로운 이미지가 있을 경우 저장
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<PostImage> postImages = request.getImageUrls().stream()
                    .map(url -> PostImage.builder()
                            .postId(postId)
                            .imageUrl(url)
                            .build())
                    .toList();

            postImageRepository.saveAll(postImages);
        }
    }


    @Transactional
    @Override
    public void deletePost(Long postId, Long userId) {
        // 1. 게시글 조회 (없으면 예외)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        // 2. 게시글 작성자가 아닌 경우 삭제 불가
        if (!post.getUserId().equals(userId)) {
            throw new CustomPostException(ErrorCode.POST_FORBIDDEN_UPDATE);
        }

        // 3. 해당 게시글에 연관된 이미지 삭제
        postImageRepository.deleteAllByPostId(postId);

        // 4. 게시글 삭제
        postRepository.delete(post);
    }




    @Transactional(readOnly = true) // 읽기 전용 트랜잭션으로 성능 최적화 및 불필요한 flush 방지
    @Override
    public PostDetailResponse getPostDetail(Long postId, Long viewerId) {

        // ✅ 게시글 ID로 조회 (없으면 커스텀 예외 발생)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        // ✅ 조회 기록이 없고 로그인 사용자일 경우에만 기록 저장 (중복 방지 목적)
        if (viewerId != null &&
                postViewRepository.findByPostIdAndUserId(postId, viewerId).isEmpty()) {
            postViewRepository.save(PostView.builder()
                    .postId(postId)       // 게시글 ID
                    .userId(viewerId)     // 조회한 사용자 ID
                    .build());
        }

        // ✅ 첨부 이미지 URL 목록 조회
        List<String> imageUrls = postImageRepository.findByPostId(postId).stream()
                .map(PostImage::getImageUrl) // PostImage 객체 → 이미지 URL
                .toList();

        // ✅ PostDetailResponse DTO로 매핑하여 응답 반환
        return PostDetailResponse.builder()
                .postId(post.getId())                         // 게시글 ID
                .title(post.getTitle())                       // 제목
                .content(post.getContent())                   // 본문
                .totalCost(post.getTotalCost())               // 지출 비용
                .isChallenge(post.getIsChallenge())           // 챌린지 여부
                .thumbnailUrl(post.getThumbnailUrl())         // 썸네일 이미지
                .imageUrls(imageUrls)                         // 첨부 이미지 리스트
                .likeCount(postLikeRepository.countByPostId(postId))     // 좋아요 수
                .commentCount(commentRepository.countByPostId(postId))   // 댓글 수
                .scrapCount(postScrapRepository.countByPostId(postId))   // 스크랩 수
                .viewCount(postViewRepository.countByPostId(postId))     // 조회 수
                .createdAt(post.getCreatedAt())               // 작성 시간
                .writerNickname(post.getWriterNickname())     // 작성자 닉네임
                .build();
    }



    /**
     * 게시글 목록 조회
     *
     * - 정렬 옵션(PostSortType)에 따라 정렬된 게시글 리스트를 페이징 처리하여 반환
     * - 챌린지 여부 필터링 가능 (challenge=true → 챌린지 글만 조회)
     * - 연관관계 없이 Post 엔티티에서 직접 작성자 닉네임 포함
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getPostList(PostSortType sort, Boolean challenge, Pageable pageable) {

        // 1. 정렬 옵션(PostSortType)을 적용한 Pageable 객체 생성
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                getSort(sort) // enum에 따라 정렬 조건을 지정
        );

        // 2. 챌린지 여부 필터링 조건 분기
        Page<Post> posts = (challenge != null)
                ? postRepository.findByIsChallenge(challenge, sortedPageable) // 챌린지 여부에 따라 필터링
                : postRepository.findAll(sortedPageable); // 전체 조회

        // 3. 조회된 게시글 Page<Post>를 Page<PostSummaryResponse>로 변환 (DTO 매핑)
        return posts.map(post -> PostSummaryResponse.builder()
                .postId(post.getId())                           // 게시글 ID
                .title(post.getTitle())                         // 제목
                .thumbnailUrl(post.getThumbnailUrl())           // 썸네일 이미지
                .isChallenge(post.getIsChallenge())             // 챌린지 여부
                .likeCount(post.getLikeCount())                 // 좋아요 수
                .commentCount(post.getCommentCount())           // 댓글 수
                .scrapCount(post.getScrapCount())               // 스크랩 수
                .writerNickname(post.getWriterNickname())       // 작성자 닉네임 (연관관계 X)
                .createdAt(post.getCreatedAt())                 // 생성일
                .build()
        );
    }



    /**
     * 게시글 정렬 조건을 반환하는 메서드
     *
     * @param type 정렬 기준 (LIKES, COMMENTS, SCRAPS, LATEST)
     * @return 정렬 기준에 따른 Sort 객체 (기본값은 최신순)
     */
    private Sort getSort(PostSortType type) {
        if (type == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt"); // 기본값: 최신순
        }

        return switch (type) {
            case LIKES -> Sort.by(Sort.Direction.DESC, "likeCount");       // 좋아요 순
            case COMMENTS -> Sort.by(Sort.Direction.DESC, "commentCount"); // 댓글 순
            case SCRAPS -> Sort.by(Sort.Direction.DESC, "scrapCount");     // 스크랩 순
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");      // 최신순
        };
    }


    /**
     * 특정 사용자가 작성한 게시글 목록을 조회하는 메서드
     *
     * @param userId 조회 대상 사용자 ID
     * @return 게시글 요약 정보 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<PostSummaryResponse> getUserPosts(Long userId) {
        // 1. 전체 게시글 중 작성자 ID 기준으로 필터링 (페이징 없이 전체 조회)
        Page<Post> userPosts = postRepository.findByUserId(userId, Pageable.unpaged());

        // 2. 게시글이 없을 경우 예외 처리 (선택 사항: 없으면 빈 리스트 반환도 가능)
        if (userPosts.isEmpty()) {
            throw new CustomPostException(ErrorCode.POST_NOT_FOUND); // "게시글이 존재하지 않습니다."
        }

        // 3. 게시글 목록을 PostSummaryResponse DTO로 변환
        return userPosts.stream()
                .map(post -> PostSummaryResponse.builder()
                        .postId(post.getId())                           // 게시글 ID
                        .title(post.getTitle())                         // 제목
                        .thumbnailUrl(post.getThumbnailUrl())           // 썸네일 이미지 URL
                        .isChallenge(post.getIsChallenge())             // 챌린지 여부
                        .likeCount(post.getLikeCount())                 // 좋아요 수
                        .commentCount(post.getCommentCount())           // 댓글 수
                        .scrapCount(post.getScrapCount())               // 스크랩 수
                        .writerNickname(post.getWriterNickname())       // 작성자 닉네임 (직접 저장된 필드 사용)
                        .createdAt(post.getCreatedAt())                 // 작성 시간
                        .build())
                .toList(); // 리스트로 반환
    }

}
