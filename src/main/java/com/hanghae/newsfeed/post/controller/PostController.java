package com.hanghae.newsfeed.post.controller;

import com.hanghae.newsfeed.post.dto.request.PostRequestDto;
import com.hanghae.newsfeed.post.dto.response.PostResponseDto;
import com.hanghae.newsfeed.post.service.PostService;
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
public class PostController {
    private final PostService postService;

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

    // 게시물 작성
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestBody PostRequestDto requestDto
    ) {
        requestDto.setUserId(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(postService.createPost(requestDto));
    }

    // 게시물 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @PathVariable Long postId,
            @RequestBody PostRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(postId, requestDto, userDetails));
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostResponseDto> deletePost(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.deletePost(postId, userDetails));
    }
}
