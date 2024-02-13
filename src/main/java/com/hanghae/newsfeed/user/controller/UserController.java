package com.hanghae.newsfeed.user.controller;

import com.hanghae.newsfeed.post.dto.response.PostResponseDto;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.dto.request.PasswordUpdateRequest;
import com.hanghae.newsfeed.user.dto.request.UserUpdateRequest;
import com.hanghae.newsfeed.user.dto.response.UserResponse;
import com.hanghae.newsfeed.user.service.UserService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    // 회원 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUser(
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(userDetails));
    }

    // 내가 작성한 게시물 조회
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> getPostsByUserId(
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getPostsByUserId(userDetails));
    }

    // 회원 정보 수정 (닉네임, 소개, 프로필 사진)
    @PatchMapping
    public ResponseEntity<UserResponse> updateUser(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDetails, request));
    }

    // 비밀번호 수정
    @PatchMapping("/password")
    public ResponseEntity<UserResponse> updatePassword(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestBody @Valid PasswordUpdateRequest request
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updatePassword(userDetails, request));
    }

    // 회원 목록 조회
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getActiveUsers());
    }
}
