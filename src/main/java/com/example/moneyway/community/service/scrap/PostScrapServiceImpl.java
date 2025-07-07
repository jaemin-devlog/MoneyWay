package com.example.moneyway.community.service.scrap;

import com.example.moneyway.common.exception.CustomException.CustomPostException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.domain.PostScrap;
import com.example.moneyway.community.repository.action.PostScrapRepository;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostScrapServiceImpl implements PostScrapService {

    private final PostRepository postRepository;
    private final PostScrapRepository postScrapRepository;
    private final UserService userService;

    @Override
    @Transactional
    public boolean toggleScrap(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));
        User user = userService.findActiveUserById(userId);

        Optional<PostScrap> existingScrap = postScrapRepository.findByPostAndUser(post, user);

        if (existingScrap.isPresent()) {
            // 스크랩이 이미 존재하면 -> 삭제 (스크랩 취소)
            postScrapRepository.delete(existingScrap.get());
            post.decreaseScrapCount();
            return false; // 스크랩 취소됨
        } else {
            // 스크랩이 없으면 -> 생성 (스크랩)
            PostScrap newScrap = PostScrap.builder()
                    .post(post)
                    .user(user)
                    .build();
            postScrapRepository.save(newScrap);
            post.increaseScrapCount();
            return true; // 스크랩 추가됨
        }
    }
}