package com.hanghae.newsfeed.comment.controller;

import com.hanghae.newsfeed.comment.dto.request.CommentRequestDto;
import com.hanghae.newsfeed.comment.dto.response.CommentResponseDto;
import com.hanghae.newsfeed.comment.service.CommentService;
import com.hanghae.newsfeed.security.UserDetailsImpl;
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
@RequestMapping("/api/posts")
public class CommentController {
    private final CommentService commentService;

    // 댓글 목록 조회
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getAllComments(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllComments(postId));
    }

    // 댓글 작성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestBody CommentRequestDto requestDto
    ) {
        requestDto.setUserId(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(commentService.createComment(postId, requestDto));
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestBody CommentRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateComment(commentId, requestDto, userDetails));
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> deleteComment(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @PathVariable Long commentId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.deleteComment(commentId, userDetails));
    }
}