package com.hanghae.newsfeed.post.service;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    /**
     * 게시물 목록 조회
     * @param pageable 페이징 정보
     * @return 게시물 목록
     */
    Page<PostResponse> getAllPosts(Pageable pageable);

    /**
     * 게시물 조회
     * @param postId 게시물 아이디
     * @return 게시물 정보
     */
    PostResponse getPost(Long postId);

    /**
     * 게시물 작성
     * @param userDetails 사용자 상세 정보
     * @param request 게시물 생성 요청
     * @return 생성된 게시물 정보
     */
    PostResponse createPost(UserDetailsImpl userDetails, PostRequest request);

    /**
     * 게시물 수정
     * @param userDetails 사용자 상세 정보
     * @param postId 게시물 아이디
     * @param request 게시물 수정 요청
     * @return 수정된 게시물 정보
     */
    PostResponse updatePost(UserDetailsImpl userDetails, Long postId, PostRequest request);

    /**
     * 게시물 삭제
     * @param userDetails 사용자 상세 정보
     * @param postId 게시물 아이디
     * @return 삭제된 게시물 정보
     */
    PostResponse deletePost(UserDetailsImpl userDetails, Long postId);
}
