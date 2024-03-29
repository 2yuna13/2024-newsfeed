package com.hanghae.newsfeed.admin.controller;

import com.hanghae.newsfeed.admin.service.impl.AdminCommentServiceImpl;
import com.hanghae.newsfeed.comment.dto.request.CommentRequest;
import com.hanghae.newsfeed.comment.dto.response.CommentResponse;
import com.hanghae.newsfeed.comment.service.impl.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admins/comments")
public class AdminCommentController {
    private final CommentServiceImpl commentService;
    private final AdminCommentServiceImpl adminCommentService;

    // 댓글 목록 조회
    @GetMapping("/{postId}")
    public ResponseEntity<Page<CommentResponse>> getAllComments(
            @PathVariable Long postId,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllComments(postId, pageable));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminCommentService.updateComment(commentId, request));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponse> deleteComment(
            @PathVariable Long commentId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminCommentService.deleteComment(commentId));
    }
}
