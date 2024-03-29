package com.hanghae.newsfeed.like.controller;

import com.hanghae.newsfeed.like.dto.response.CommentLikeResponse;
import com.hanghae.newsfeed.like.service.impl.CommentLikeServiceImpl;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/likes")
public class CommentLikeController {
    private final CommentLikeServiceImpl commentLikeService;

    // 댓글 좋아요
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<CommentLikeResponse> likeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentLikeService.likeComment(userDetails, commentId));
    }

    // 댓글 좋아요 취소
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommentLikeResponse> unLikeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentLikeService.unlikeComment(userDetails, commentId));
    }
}
