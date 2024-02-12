package com.hanghae.newsfeed.like.service;

import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.comment.repository.CommentRepository;
import com.hanghae.newsfeed.like.dto.response.CommentLikeResponseDto;
import com.hanghae.newsfeed.like.entity.CommentLike;
import com.hanghae.newsfeed.like.repository.CommentLikeRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 댓글 좋아요
    @Transactional
    public CommentLikeResponseDto likeComment(UserDetailsImpl userDetails, Long commentId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("댓글 좋아요 실패, 등록된 사용자가 없습니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 좋아요 실패, 등록된 댓글이 없습니다."));

        // 작성자 본인이 아닌지 확인
        if (!user.equals(comment.getUser())) {
            // 기존 좋아요 여부 확인
            if (commentLikeRepository.existsByUserAndComment(user, comment)) {
                throw new IllegalArgumentException("댓글 좋아요 실패, 이미 좋아요를 누른 댓글입니다.");
            }

            // 좋아요 등록
            CommentLike commentLike = new CommentLike(user, comment);
            CommentLike savedCommentLike = commentLikeRepository.save(commentLike);

            return new CommentLikeResponseDto(savedCommentLike.getId(), user.getId(), comment.getId(), "댓글 좋아요 성공");
        } else {
            throw new IllegalArgumentException("댓글 좋아요 실패, 자신의 댓글에는 좋아요를 누를 수 없습니다.");
        }
    }

    // 댓글 좋아요 취소
    @Transactional
    public CommentLikeResponseDto unlikeComment(UserDetailsImpl userDetails, Long commentId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("댓글 좋아요 실패, 등록된 사용자가 없습니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 좋아요 실패, 등록된 댓글이 없습니다."));

        // 기존 좋아요 여부 확인
        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment);

            commentLikeRepository.delete(commentLike);

            return new CommentLikeResponseDto(commentLike.getId(), user.getId(), comment.getId(), "댓글 좋아요 취소 성공");
        } else {
            throw new IllegalArgumentException("댓글 좋아요 취소 실패, 해당 댓글에 좋아요를 누르지 않았습니다.");
        }
    }
}
