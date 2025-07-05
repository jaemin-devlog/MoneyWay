package com.example.moneyway.community.service.comment;

import com.example.moneyway.common.exception.CustomException.CustomPostException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.community.domain.Comment;
import com.example.moneyway.community.domain.Post;
import com.example.moneyway.community.dto.request.CreateCommentRequest;
import com.example.moneyway.community.dto.response.CommentResponse;
import com.example.moneyway.community.dto.response.common.WriterInfo;
import com.example.moneyway.community.repository.comment.CommentRepository;
import com.example.moneyway.community.repository.post.PostRepository;
import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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

        User user = userService.findActiveUserById(userId);

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .build();
        commentRepository.save(comment);

        post.increaseCommentCount();

        return comment.getId();
    }

    /**
     * 댓글 삭제 (소프트 삭제)
     */
    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomPostException(ErrorCode.COMMENT_FORBIDDEN_DELETE);
        }

        if (!comment.isDeleted()) {
            comment.delete();
            Post post = comment.getPost();
            post.decreaseCommentCount();
        }
    }

    /**
     * 삭제되지 않은 댓글 목록을 조회합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getActiveCommentsByPostId(Long postId, Long viewerId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomPostException(ErrorCode.POST_NOT_FOUND);
        }

        List<Comment> comments = commentRepository.findByPost_IdAndDeletedFalseOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(comment -> toCommentResponse(comment, viewerId)) // viewerId를 함께 전달
                .toList();
    }

    /**
     * Comment 엔티티를 CommentResponse DTO로 변환하는 헬퍼 메서드
     */
    private CommentResponse toCommentResponse(Comment comment, Long viewerId) {
        User writer = comment.getUser();

        // [개선] WriterInfo 공통 DTO를 사용하여 작성자 정보를 만듭니다.
        WriterInfo writerInfo = WriterInfo.builder()
                .userId(writer.getId())
                .nickname(writer.getNickname())
                .build();

        return CommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .writerInfo(writerInfo) // writerNickname 대신 writerInfo 객체 사용
                .isMine(Objects.equals(writer.getId(), viewerId)) // [개선] 내가 쓴 댓글인지 여부 계산
                .build();
    }
}