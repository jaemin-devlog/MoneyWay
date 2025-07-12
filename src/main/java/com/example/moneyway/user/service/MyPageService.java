package com.example.moneyway.user.service;

import com.example.moneyway.auth.token.repository.RefreshTokenRepository;
import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.community.dto.response.common.WriterInfo;
import com.example.moneyway.community.repository.action.PostLikeRepository;
import com.example.moneyway.community.repository.action.PostScrapRepository;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.dto.response.MyPageResponse;
import com.example.moneyway.user.dto.response.UserResponse;
import com.example.moneyway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        // 1. 삭제할 사용자 조회
        User user = userService.findByEmail(email);

        // ✅ 2. 사용자의 Refresh Token을 DB에서 삭제하여 재로그인 방지
        refreshTokenRepository.deleteByUser(user);

        user.withdraw();
    }
    /**
     * ✅ [추가] 로그인한 사용자의 비밀번호를 변경합니다.
     * @param email 현재 로그인된 사용자의 이메일
     * @param currentPassword 사용자가 입력한 현재 비밀번호
     * @param newPassword 사용자가 설정할 새 비밀번호
     */
    public void changePassword(String email, String currentPassword, String newPassword) {
        // 1. 사용자 정보 조회
        User user = userService.findByEmail(email);

        // 2. 현재 비밀번호가 맞는지 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomUserException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        // 3. 새 비밀번호가 이전 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new CustomUserException(ErrorCode.PASSWORD_SAME_AS_BEFORE);
        }

        // 4. 모든 검증 통과 시, 새 비밀번호를 암호화하여 업데이트
        user.updatePassword(passwordEncoder.encode(newPassword));
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
        return postScrapRepository.findAllByUserWithPaging(user, pageable)
                .map(postScrap -> toPostSummaryResponse(postScrap.getPost(), user));
    }

    /**
     * ✅ [신규] 마이페이지 초기 진입 시 필요한 모든 정보를 한번에 조회합니다.
     * @param email 현재 사용자 이메일
     * @return 사용자 정보, 내가 쓴 글 첫 페이지, 내가 스크랩한 글 첫 페이지
     */
    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(String email) {
        // 1. 사용자 정보 조회
        UserResponse userInfo = UserResponse.from(userService.findByEmail(email));

        // 2. 내가 쓴 글/스크랩한 글의 첫 페이지만 조회 (예: 5개씩)
        Pageable initialPageable = PageRequest.of(0, 5);
        Page<PostSummaryResponse> myPosts = getMyPosts(email, initialPageable);
        Page<PostSummaryResponse> myScraps = getMyScraps(email, initialPageable);

        // 3. 조회한 모든 정보를 하나의 DTO에 담아 반환
        return MyPageResponse.builder()
                .userInfo(userInfo)
                .myPosts(myPosts)
                .myScraps(myScraps)
                .build();
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