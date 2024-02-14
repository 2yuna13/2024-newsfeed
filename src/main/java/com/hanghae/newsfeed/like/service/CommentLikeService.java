package com.hanghae.newsfeed.like.service;

import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.comment.repository.CommentRepository;
import com.hanghae.newsfeed.common.exception.HttpException;
import com.hanghae.newsfeed.like.dto.response.CommentLikeResponse;
import com.hanghae.newsfeed.like.entity.CommentLike;
import com.hanghae.newsfeed.like.repository.CommentLikeRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 댓글 좋아요
    @Transactional
    public CommentLikeResponse likeComment(UserDetailsImpl userDetails, Long commentId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new HttpException(false, "등록된 댓글이 없습니다.", HttpStatus.NOT_FOUND));

        // 작성자 본인이 아닌지 확인
        if (!user.equals(comment.getUser())) {
            // 기존 좋아요 여부 확인
            if (commentLikeRepository.existsByUserAndComment(user, comment)) {
                throw new HttpException(false, "이미 좋아요를 누른 댓글입니다.", HttpStatus.BAD_REQUEST);
            }

            // 좋아요 등록
            CommentLike commentLike = new CommentLike(user, comment);
            commentLikeRepository.save(commentLike);

            return new CommentLikeResponse(user.getId(), comment.getId(), "댓글 좋아요 성공");
        } else {
            throw new HttpException(false, "자신의 댓글에는 좋아요를 누를 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 댓글 좋아요 취소
    @Transactional
    public CommentLikeResponse unlikeComment(UserDetailsImpl userDetails, Long commentId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new HttpException(false, "등록된 댓글이 없습니다.", HttpStatus.NOT_FOUND));

        // 기존 좋아요 여부 확인
        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment);

            commentLikeRepository.delete(commentLike);

            return new CommentLikeResponse(user.getId(), comment.getId(), "댓글 좋아요 취소 성공");
        } else {
            throw new HttpException(false, "해당 댓글에 좋아요를 누르지 않았습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
