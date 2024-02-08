package com.hanghae.newsfeed.follow.controller;

import com.hanghae.newsfeed.follow.dto.response.FollowResponseDto;
import com.hanghae.newsfeed.follow.service.FollowService;
import com.hanghae.newsfeed.security.UserDetailsImpl;
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
    public ResponseEntity<FollowResponseDto> followUser(
            @PathVariable Long followingId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.followUser(followingId, userDetails));
    }

    // 팔로우 취소
    @DeleteMapping("/{followingId}")
    public ResponseEntity<FollowResponseDto> unfollowUser(
            @PathVariable Long followingId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.unfollowUser(followingId, userDetails));
    }

    // 팔로잉 목록 조회
    @GetMapping("/followings")
    public ResponseEntity<List<FollowResponseDto>> getFollowingList(@AuthenticationPrincipal final UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.followingList(userDetails));
    }

    // 팔로워 목록 조회
    @GetMapping("/followers")
    public ResponseEntity<List<FollowResponseDto>> getFollowerList(@AuthenticationPrincipal final UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.followerList(userDetails));
    }
}
