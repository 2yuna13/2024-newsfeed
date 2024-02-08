package com.hanghae.newsfeed.admin.controller;

import com.hanghae.newsfeed.admin.service.AdminPostService;
import com.hanghae.newsfeed.post.dto.request.PostRequestDto;
import com.hanghae.newsfeed.post.dto.response.PostResponseDto;
import com.hanghae.newsfeed.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admins/posts")
public class AdminPostController {
    private final PostService postService;
    private final AdminPostService adminPostService;

    // 게시물 목록 조회
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts());
    }

    // 게시물 조회
    @GetMapping("{postId}")
    public ResponseEntity<PostResponseDto> getPost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId));
    }

    // 게시물 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminPostService.updatePost(postId, requestDto));
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostResponseDto> deletePost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminPostService.deletePost(postId));
    }
}
