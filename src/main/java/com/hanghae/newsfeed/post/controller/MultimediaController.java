package com.hanghae.newsfeed.post.controller;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.post.dto.response.MultimediaResponse;
import com.hanghae.newsfeed.post.service.MultimediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts/multimedia")
public class MultimediaController {
    private final MultimediaService multimediaService;

    // 게시물 멀티미디어 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<MultimediaResponse>> getMultimediaList(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(multimediaService.getMultimediaList(postId));
    }

    // 게시물 멀티미디어 수정(추가)
    @PatchMapping("/{postId}")
    public ResponseEntity<List<MultimediaResponse>> updatePostMultimedia(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @PathVariable Long postId,
            @RequestPart(name = "files") List<MultipartFile> files
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(multimediaService.updatePostMultimedia(userDetails, postId, files));
    }

    // 게시물 멀티미디어 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<List<MultimediaResponse>> deleteMultimedia(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(multimediaService.deleteMultimedia(userDetails, postId));
    }
}
