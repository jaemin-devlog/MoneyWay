package com.example.moneyway.user.service;

import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.dto.response.common.WriterInfo;
import com.example.moneyway.community.repository.action.PostLikeRepository;
import com.example.moneyway.community.repository.action.PostScrapRepository;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 닉네임 변경, 회원 탈퇴, 내가 쓴 글/스크랩 조회 등 마이페이지 관련 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    // User 관련
    private final UserService userService;
    private final UserRepository userRepository;

    // Community 관련
    private final PostRepository postRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostLikeRepository postLikeRepository; // isLiked 상태 확인을 위해 추가

    /**
     * 사용자 닉네임을 변경합니다.
     */
    public void updateNickname(String email, String newNickname) {
        if (userRepository.existsByNickname(newNickname)) {
            throw new CustomUserException(ErrorCode.DUPLICATE_NICKNAME);
        }
        User user = userService.findByEmail(email);
        user.updateProfile(newNickname, user.getProfileImageUrl());
    }

    /**
     * 사용자를 탈퇴 처리합니다.
     */
    public void withdrawUser(String email) {
        User user = userService.findByEmail(email);
        user.withdraw();
    }

    /**
     * 현재 인증된 사용자가 작성한 글 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getMyPosts(String email, Pageable pageable) {
        User user = userService.findByEmail(email);
        Page<Post> myPosts = postRepository.findByUser(user, pageable);
        return myPosts.map(post -> toPostSummaryResponse(post, user));
    }

    /**
     * 현재 인증된 사용자가 스크랩한 글 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<PostSummaryResponse> getMyScraps(String email, Pageable pageable) {
        User user = userService.findByEmail(email);
        // PostScrapRepository에 페이징 조회를 위한 메서드가 구현되어 있어야 합니다.
        return postScrapRepository.findAllByUserWithPaging(user, pageable)
                .map(postScrap -> toPostSummaryResponse(postScrap.getPost(), user));
    }

    /**
     * Post 엔티티를 PostSummaryResponse DTO로 변환하는 헬퍼 메서드
     */
    private PostSummaryResponse toPostSummaryResponse(Post post, User currentUser) {
        // 현재 로그인한 사용자가 이 게시글을 스크랩했는지 DB에 확인합니다.
        boolean isScrapped = postScrapRepository.existsByPostAndUser(post, currentUser);
        // 현재 로그인한 사용자가 이 게시글에 좋아요를 눌렀는지 DB에 확인합니다.
        boolean isLiked = postLikeRepository.existsByPostAndUser(post, currentUser);

        // PostSummaryResponse 객체를 빌더 패턴으로 생성 시작합니다.
        return PostSummaryResponse.builder()
                .postId(post.getId()) // 게시글의 고유 ID를 DTO에 설정합니다.
                .title(post.getTitle()) // 게시글의 제목을 DTO에 설정합니다.
                .thumbnailUrl(post.getThumbnailUrl()) // 게시글의 썸네일 이미지 URL을 DTO에 설정합니다.
                .isChallenge(post.isChallenge()) // 이 게시글이 챌린지 게시글인지 여부를 DTO에 설정합니다.
                .createdAt(post.getCreatedAt()) // 게시글의 생성 시간을 DTO에 설정합니다.
                .writerInfo(WriterInfo.from(post.getUser())) // 게시글 작성자(User) 정보를 WriterInfo DTO로 변환하여 설정합니다.
                .likeCount(post.getLikeCount()) // 게시글의 전체 좋아요 개수를 DTO에 설정합니다.
                .commentCount(post.getCommentCount()) // 게시글의 전체 댓글 개수를 DTO에 설정합니다.
                .scrapCount(post.getScrapCount()) // 게시글의 전체 스크랩 개수를 DTO에 설정합니다.
                .isLiked(isLiked) // 위에서 계산한 '현재 사용자의 좋아요 여부'를 DTO에 설정합니다.
                .isScrapped(isScrapped) // 위에서 계산한 '현재 사용자의 스크랩 여부'를 DTO에 설정합니다.
                .build(); // 모든 설정이 완료된 PostSummaryResponse 객체를 생성하여 반환합니다.
    }
}