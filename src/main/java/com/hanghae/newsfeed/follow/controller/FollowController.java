package com.hanghae.newsfeed.follow.controller;

import com.hanghae.newsfeed.follow.dto.response.FollowResponseDto;
import com.hanghae.newsfeed.follow.service.FollowService;
import com.hanghae.newsfeed.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
