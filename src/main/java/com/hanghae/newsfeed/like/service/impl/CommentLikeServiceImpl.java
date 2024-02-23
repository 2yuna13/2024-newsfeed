package com.hanghae.newsfeed.like.service.impl;

import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.comment.repository.CommentRepository;
import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.like.dto.response.CommentLikeResponse;
import com.hanghae.newsfeed.like.entity.CommentLike;
import com.hanghae.newsfeed.like.repository.CommentLikeRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.like.service.CommentLikeService;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 댓글 좋아요
    @Override
    @Transactional
    public CommentLikeResponse likeComment(UserDetailsImpl userDetails, Long commentId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));

        // 작성자 본인이 아닌지 확인
        if (!user.equals(comment.getUser())) {
            // 기존 좋아요 여부 확인
            if (commentLikeRepository.existsByUserAndComment(user, comment)) {
                throw new CustomException(CustomErrorCode.ALREADY_LIKED);
            }

            // 좋아요 등록
            CommentLike commentLike = new CommentLike(user, comment);
            commentLikeRepository.save(commentLike);

            return new CommentLikeResponse(user.getId(), comment.getId(), "댓글 좋아요 성공");
        } else {
            throw new CustomException(CustomErrorCode.CANNOT_LIKE_OWN_CONTENT);
        }
    }

    // 댓글 좋아요 취소
    @Override
    @Transactional
    public CommentLikeResponse unlikeComment(UserDetailsImpl userDetails, Long commentId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));

        // 기존 좋아요 여부 확인
        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment);

            commentLikeRepository.delete(commentLike);

            return new CommentLikeResponse(user.getId(), comment.getId(), "댓글 좋아요 취소 성공");
        } else {
            throw new CustomException(CustomErrorCode.NO_LIKE_YET);
        }
    }
}
