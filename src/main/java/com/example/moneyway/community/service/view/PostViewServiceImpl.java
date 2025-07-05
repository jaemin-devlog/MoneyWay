package com.example.moneyway.community.service.view;

import com.example.moneyway.common.exception.CustomException.CustomPostException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.domain.PostView;
import com.example.moneyway.community.repository.action.PostViewRepository;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostViewServiceImpl implements PostViewService {

    private final PostRepository postRepository;
    private final PostViewRepository postViewRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void increaseViewCount(Long postId, Long viewerId, String ipAddress) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        // 조회수 중복 방지를 위해 1시간 이내 조회 기록 확인
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        boolean alreadyViewed;
        User viewer = null;

        if (viewerId != null) {
            viewer = userService.findActiveUserById(viewerId);
            alreadyViewed = postViewRepository.existsByPostAndUserAndViewedAtAfter(post, viewer, oneHourAgo);
        } else {
            alreadyViewed = postViewRepository.existsByPostAndIpAddressAndViewedAtAfter(post, ipAddress, oneHourAgo);
        }

        if (!alreadyViewed) {
            PostView postView = PostView.builder()
                    .post(post)
                    .user(viewer) // viewer가 null일 수 있음
                    .ipAddress(ipAddress)
                    .build();
            postViewRepository.save(postView);
            post.increaseViewCount();
        }
    }
}