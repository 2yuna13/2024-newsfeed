package com.hanghae.newsfeed.like.service;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.like.dto.response.PostLikeResponse;

public interface PostLikeService {
    /**
     * 게시물 좋아요
     * @param userDetails 사용자 상세 정보
     * @param postId 게시물 아이디
     * @return 좋아요 결과
     */
    PostLikeResponse likePost(UserDetailsImpl userDetails, Long postId);

    /**
     * 게시물 좋아요 취소
     * @param userDetails 사용자 상세 정보
     * @param postId 게시물 아이디
     * @return 좋아요 취소 결과
     */
    PostLikeResponse unlikePost(UserDetailsImpl userDetails, Long postId);
}
