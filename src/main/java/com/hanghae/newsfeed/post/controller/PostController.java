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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    // 게시물 작성
    @PostMapping
    public ResponseEntity<PostResponseDto> create(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestBody PostRequestDto requestDto
    ) {
        requestDto.setUserId(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(postService.create(requestDto));
    }

    // 게시물 수정
    @PatchMapping("/{id}")
    public ResponseEntity<PostResponseDto> update(
            @PathVariable Long id,
            @RequestBody PostRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.update(id, requestDto));
    }

    // 게시물 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<PostResponseDto> delete(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.delete(id));
    }
}
