package com.example.moneyway.community.service.comment;

import com.example.moneyway.community.domain.Comment;
import com.example.moneyway.community.dto.request.CreateCommentRequest;
import com.example.moneyway.community.dto.response.CommentResponse;
import com.example.moneyway.community.repository.comment.CommentRepository;
import com.example.moneyway.community.repository.comment.CommentRepository;
import com.example.moneyway.common.exception.CustomException.CustomPostException;
import com.example.moneyway.common.exception.ErrorCode;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;

    @Transactional
    @Override
    public Long createComment(Long userId, CreateCommentRequest request) {
        // ✅ 사용자 닉네임 조회 (예외 처리 포함)
        String nickname = userService.getNicknameById(userId); // 인증된 사용자 ID로 닉네임 조회

        // ⛔ 닉네임이 없거나 비어있을 경우 예외 발생
        if (nickname == null || nickname.isBlank()) {
            throw new CustomPostException(ErrorCode.USER_NICKNAME_NOT_FOUND); // 사용자 닉네임 없음 예외
        }

        // ✅ 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .postId(request.getPostId())      // 연결된 게시글 ID
                .userId(userId)                   // 댓글 작성자 ID
                .content(request.getContent())    // 댓글 내용
                .writerNickname(nickname)         // 작성자 닉네임 저장
                .isDeleted(false)                 // 소프트 삭제 기본값 false
                .createdAt(LocalDateTime.now())   // 생성 시간 설정
                .build();

        // ✅ 댓글 저장 후 ID 반환
        return commentRepository.save(comment).getId(); // 저장된 댓글의 ID 반환
    }


    @Transactional
    @Override
    public void deleteComment(Long commentId, Long userId) {
        // ✅ 댓글 ID로 댓글 조회 (없으면 예외 발생)
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomPostException(ErrorCode.COMMENT_NOT_FOUND)); // 존재하지 않는 댓글

        // ⛔ 삭제 권한 확인 (작성자가 아니면 예외 발생)
        if (!comment.getUserId().equals(userId)) {
            throw new CustomPostException(ErrorCode.COMMENT_FORBIDDEN_DELETE); // 본인 댓글만 삭제 가능
        }

        // ✅ 소프트 삭제 처리 (isDeleted = true)
        comment.setIsDeleted(true);
    }


    @Transactional(readOnly = true) // 읽기 전용 트랜잭션: 성능 최적화 및 불필요한 락 방지
    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {

        // 1. 해당 게시글의 삭제되지 않은 댓글 목록 조회
        List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalse(postId);

        // 2. 댓글 목록이 비어 있으면 예외 처리 (필요한 경우)
        if (comments.isEmpty()) {
            throw new CustomPostException(ErrorCode.COMMENT_NOT_FOUND); // 예외코드: 댓글 없음
        }

        // 3. 댓글 목록을 CommentResponse DTO 리스트로 변환
        return comments.stream()
                .map(comment -> CommentResponse.builder()
                        .commentId(comment.getId())                  // 댓글 ID
                        .content(comment.getContent())              // 댓글 내용
                        .writerNickname(comment.getWriterNickname())// 작성자 닉네임
                        .createdAt(comment.getCreatedAt())          // 작성 시간
                        .build())
                .toList(); // 최종 리스트로 반환
    }

}
