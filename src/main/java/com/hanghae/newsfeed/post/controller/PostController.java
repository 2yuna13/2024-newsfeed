package com.hanghae.newsfeed.post.controller;

import com.hanghae.newsfeed.post.dto.request.PostRequestDto;
import com.hanghae.newsfeed.post.dto.response.PostResponseDto;
import com.hanghae.newsfeed.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    // 게시물 작성
    @PostMapping
    public ResponseEntity<PostResponseDto> create(@RequestBody PostRequestDto requestDto) {
        requestDto.setUserId(1L); // 로그인 후 정보 받아오기 구현해야함.

        return ResponseEntity.status(HttpStatus.OK).body(postService.create(requestDto));
    }
}
