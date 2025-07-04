package com.example.moneyway.community.service.comment;

import com.example.moneyway.common.exception.CustomException.CustomPostException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.community.domain.Comment;
import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.dto.request.CreateCommentRequest;
import com.example.moneyway.community.dto.response.CommentResponse;
import com.example.moneyway.community.repository.comment.CommentRepository;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    /**
     * 댓글 생성
     */
    @Override
    public Long createComment(Long userId, CreateCommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new CustomPostException(ErrorCode.POST_NOT_FOUND));

        String nickname = userService.getNicknameById(userId);
        if (nickname == null || nickname.isBlank()) {
            throw new CustomPostException(ErrorCode.USER_NICKNAME_NOT_FOUND);
        }

        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .userId(userId)
                .content(request.getContent())
                .writerNickname(nickname)
                .build();
        commentRepository.save(comment);

        // [반영] 게시글의 댓글 수를 증가시키고, 변경 사항을 명시적으로 저장합니다.
        post.increaseCommentCount();
        postRepository.save(post);

        return comment.getId();
    }

    /**
     * 댓글 삭제 (소프트 삭제)
     */
    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new CustomPostException(ErrorCode.COMMENT_FORBIDDEN_DELETE);
        }

        if (!comment.getIsDeleted()) {
            comment.setIsDeleted(true);

            // [반영] 연관된 게시글을 찾아 댓글 수를 감소시키고, 변경 사항을 명시적으로 저장합니다.
            postRepository.findById(comment.getPostId()).ifPresent(post -> {
                post.decreaseCommentCount();
                postRepository.save(post);
            });
        }
    }

    /**
     * [반영] "삭제되지 않은" 댓글 목록을 조회한다는 의미를 명확히 하기 위해 메서드 이름을 변경합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getActiveCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomPostException(ErrorCode.POST_NOT_FOUND);
        }

        List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalse(postId);

        return comments.stream()
                .map(this::toCommentResponse)
                .toList();
    }

    private CommentResponse toCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .writerNickname(comment.getWriterNickname())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}