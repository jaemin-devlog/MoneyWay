package com.example.moneyway.community.service.like;

import com.example.moneyway.common.exception.CustomException.CustomPostException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.domain.PostLike;
import com.example.moneyway.community.repository.action.PostLikeRepository;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserService userService;

    @Override
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));
        User user = userService.findActiveUserById(userId);

        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            // 좋아요가 이미 존재하면 -> 삭제 (좋아요 취소)
            postLikeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            return false; // 좋아요 취소됨
        } else {
            // 좋아요가 없으면 -> 생성 (좋아요)
            PostLike newLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            postLikeRepository.save(newLike);
            post.increaseLikeCount();
            return true; // 좋아요 추가됨
        }
    }
}