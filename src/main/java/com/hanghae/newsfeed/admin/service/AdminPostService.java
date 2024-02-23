package com.hanghae.newsfeed.admin.service;

import com.hanghae.newsfeed.post.dto.request.PostRequest;
import com.hanghae.newsfeed.post.dto.response.PostResponse;

public interface AdminPostService {
    /**
     * 게시물 수정
     * @param postId 게시물 아이디
     * @param request 게시물 수정 요청
     * @return 수정된 게시물 정보
     */
    PostResponse updatePost(Long postId, PostRequest request);

    /**
     * 게시물 삭제
     * @param postId 게시물 아이디
     * @return 삭제된 게시물 정보
     */
    PostResponse deletePost(Long postId);
}
