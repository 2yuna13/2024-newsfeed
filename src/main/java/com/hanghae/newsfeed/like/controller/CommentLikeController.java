package com.hanghae.newsfeed.like.controller;

import com.hanghae.newsfeed.like.dto.response.CommentLikeResponseDto;
import com.hanghae.newsfeed.like.service.CommentLikeService;
import com.hanghae.newsfeed.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/likes")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    // 댓글 좋아요
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<CommentLikeResponseDto> likeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentLikeService.likeComment(userDetails, commentId));
    }
}
