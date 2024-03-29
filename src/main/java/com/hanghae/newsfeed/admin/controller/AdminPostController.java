package com.hanghae.newsfeed.admin.controller;

import com.hanghae.newsfeed.admin.service.impl.AdminPostServiceImpl;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
import com.hanghae.newsfeed.post.dto.response.MultimediaResponse;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.service.impl.MultimediaServiceImpl;
import com.hanghae.newsfeed.post.service.impl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admins/posts")
public class AdminPostController {
    private final PostServiceImpl postService;
    private final AdminPostServiceImpl adminPostService;
    private final MultimediaServiceImpl multimediaService;

    // 게시물 목록 조회
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(required = false) String keyword,
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
