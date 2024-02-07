package com.hanghae.newsfeed.like.controller;

import com.hanghae.newsfeed.like.dto.response.PostLikeResponseDto;
import com.hanghae.newsfeed.like.service.PostLikeService;
import com.hanghae.newsfeed.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/likes")
public class PostLikeController {
    private final PostLikeService postLikeService;

    // 게시물 좋아요
    @PostMapping("/posts/{postId}")
    public ResponseEntity<PostLikeResponseDto> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(postLikeService.likePost(userDetails, postId));
    }
}
