package com.hanghae.newsfeed.follow.controller;

import com.hanghae.newsfeed.follow.dto.response.FollowResponse;
import com.hanghae.newsfeed.follow.service.FollowService;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {
    private final FollowService followService;

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
    public ResponseEntity<List<FollowResponse>> getFollowingList(@AuthenticationPrincipal final UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.followingList(userDetails));
    }

    // 팔로워 목록 조회
    @GetMapping("/followers")
    public ResponseEntity<List<FollowResponse>> getFollowerList(@AuthenticationPrincipal final UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.followerList(userDetails));
    }

    // 내가 팔로우한 유저들이 작성한 게시물 조회
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getPostsFromFollowingUsers(
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.getPostsFromFollowingUsers(userDetails));
    }
}
