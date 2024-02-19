package com.hanghae.newsfeed.like.service;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.like.dto.response.CommentLikeResponse;
import com.hanghae.newsfeed.like.dto.response.PostLikeResponse;

public interface CommentLikeService {
    /**
     * 댓글 좋아요
     * @param userDetails 사용자 상세 정보
     * @param commentId 댓글 아이디
     * @return 좋아요 결과
     */
    CommentLikeResponse likeComment(UserDetailsImpl userDetails, Long commentId);

    /**
     * 댓글 좋아요 취소
     * @param userDetails 사용자 상세 정보
     * @param commentId 댓글 아이디
     * @return 좋아요 취소 결과
     */
    CommentLikeResponse unlikeComment(UserDetailsImpl userDetails, Long commentId);
}
