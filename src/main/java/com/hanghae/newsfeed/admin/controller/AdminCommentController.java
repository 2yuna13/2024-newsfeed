package com.hanghae.newsfeed.admin.controller;

import com.hanghae.newsfeed.admin.service.AdminCommentService;
import com.hanghae.newsfeed.comment.dto.request.CommentRequestDto;
import com.hanghae.newsfeed.comment.dto.response.CommentResponseDto;
import com.hanghae.newsfeed.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admins/comments")
public class AdminCommentController {
    private final CommentService commentService;
    private final AdminCommentService adminCommentService;

    // 댓글 목록 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getAllComments(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllComments(postId));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminCommentService.updateComment(commentId, requestDto));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> deleteComment(
            @PathVariable Long commentId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminCommentService.deleteComment(commentId));
    }
}
