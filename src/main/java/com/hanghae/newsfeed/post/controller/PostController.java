package com.hanghae.newsfeed.post.controller;

import com.hanghae.newsfeed.common.annotation.RunningTime;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.service.impl.PostServiceImpl;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostServiceImpl postService;

    // 게시물 목록 조회
    @RunningTime
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(required = false) String keyword,
            @PageableDefault(value = 10)
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts(keyword, pageable));
    }

    // 게시물 조회
    @GetMapping("{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId));
    }

    // 게시물 작성
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestBody @Valid PostRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(userDetails, request));
    }

    // 게시물 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @PathVariable Long postId,
            @RequestBody PostRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(userDetails, postId, request));
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostResponse> deletePost(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(userDetails, postId));
    }
}
