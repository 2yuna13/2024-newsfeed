package com.hanghae.newsfeed.follow.controller;

import com.hanghae.newsfeed.follow.dto.response.FollowResponse;
import com.hanghae.newsfeed.follow.service.impl.FollowServiceImpl;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {
    private final FollowServiceImpl followService;

    // 팔로우
    @PostMapping("/{followingId}")
    public ResponseEntity<FollowResponse> followUser(
            @PathVariable Long followingId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.followUser(followingId, userDetails));
    }

    // 팔로우 취소
    @DeleteMapping("/{followingId}")
    public ResponseEntity<FollowResponse> unfollowUser(
            @PathVariable Long followingId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.unfollowUser(followingId, userDetails));
    }

    // 팔로잉 목록 조회
    @GetMapping("/followings")
    public ResponseEntity<Page<FollowResponse>> getFollowingList(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @SortDefault(sort = "following_id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.followingList(userDetails, pageable));
    }

    // 팔로워 목록 조회
    @GetMapping("/followers")
    public ResponseEntity<Page<FollowResponse>> getFollowerList(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @SortDefault(sort = "follower_id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.followerList(userDetails, pageable));
    }

    // 내가 팔로우한 유저들이 작성한 게시물 조회
    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponse>> getPostsFromFollowingUsers(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.getPostsFromFollowingUsers(userDetails, pageable));
    }
}
