package com.hanghae.newsfeed.admin.controller;

import com.hanghae.newsfeed.admin.service.AdminPostService;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
import com.hanghae.newsfeed.post.dto.response.MultimediaResponse;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.service.MultimediaService;
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
    private final MultimediaService multimediaService;

    // 게시물 목록 조회
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts());
    }

    // 게시물 조회
    @GetMapping("{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId));
    }

    // 게시물 멀티미디어 조회
    @GetMapping("/multimedia/{postId}")
    public ResponseEntity<List<MultimediaResponse>> getMultimediaList(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(multimediaService.getMultimediaList(postId));
    }

    // 게시물 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminPostService.updatePost(postId, request));
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostResponse> deletePost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminPostService.deletePost(postId));
    }
}
