package com.hanghae.newsfeed.follow.service;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.follow.dto.response.FollowResponse;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FollowService {
    /**
     * 팔로우
     * @param followingId 팔로우 대상 ID
     * @param userDetails 사용자 상세 정보
     * @return 팔로우 결과
     */
    FollowResponse followUser(Long followingId, UserDetailsImpl userDetails);

    /**
     * 팔로우 취소
     * @param followingId 팔로우 취소 대상 ID
     * @param userDetails 사용자 상세 정보
     * @return 팔로우 취소 결과
     */
    FollowResponse unfollowUser(Long followingId, UserDetailsImpl userDetails);

    /**
     * 팔로잉 목록 조회
     * @param userDetails 사용자 상세 정보
     * @param pageable 페이징 정보
     * @return 팔로잉 목록
     */
    Page<FollowResponse> followingList(UserDetailsImpl userDetails, Pageable pageable);

    /**
     * 팔로워 목록 조회
     * @param userDetails 사용자 상세 정보
     * @param pageable 페이징 정보
     * @return 팔로워 목록
     */
    Page<FollowResponse> followerList(UserDetailsImpl userDetails, Pageable pageable);

    /**
     * 팔로우한 유저들이 작성한 게시물 조회
     * @param userDetails 사용자 상세 정보
     * @param pageable 페이징 정보
     * @return 팔로우한 유저들의 게시물 목록
     */
    Page<PostResponse> getPostsFromFollowingUsers(UserDetailsImpl userDetails, Pageable pageable);
}
