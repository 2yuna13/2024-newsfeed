package com.hanghae.newsfeed.admin.service;

import com.hanghae.newsfeed.comment.dto.request.CommentRequestDto;
import com.hanghae.newsfeed.comment.dto.response.CommentResponseDto;
import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.comment.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCommentService {
    private final CommentRepository commentRepository;

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto) {
        // 댓글 조회 예외 발생
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(" 댓글 수정 실패, 해당 댓글이 없습니다."));

        // 댓글 수정
        target.patch(requestDto);

        // DB로 갱신
        Comment updatedComment = commentRepository.save(target);

        return CommentResponseDto.createCommentDto(updatedComment, "댓글 수정 성공");
    }

    // 댓글 삭제
    @Transactional
    public CommentResponseDto deleteComment(Long commentId) {
        // 댓글 조회 예외 발생
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(" 댓글 삭제 실패, 해당 댓글이 없습니다."));

        // 댓글 삭제
        commentRepository.delete(target);

        return CommentResponseDto.createCommentDto(target, "댓글 삭제 성공");
    }

}
