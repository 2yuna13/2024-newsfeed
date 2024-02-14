package com.hanghae.newsfeed.comment.controller;

import com.hanghae.newsfeed.comment.dto.request.CommentRequest;
import com.hanghae.newsfeed.comment.dto.response.CommentResponse;
import com.hanghae.newsfeed.comment.service.CommentService;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponse>> getAllComments(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllComments(postId));
    }

    // 댓글 작성
    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestBody CommentRequest request
    ) {
        requestDto.setUserId(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(commentService.createComment(postId, requestDto));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @PathVariable Long commentId,
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateComment(commentId, requestDto, userDetails));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponse> deleteComment(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @PathVariable Long commentId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.deleteComment(commentId, userDetails));
    }
}