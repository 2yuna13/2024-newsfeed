package com.hanghae.newsfeed.user.controller;

import com.hanghae.newsfeed.post.dto.response.PostResponseDto;
import com.hanghae.newsfeed.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.dto.request.UserRequestDto;
import com.hanghae.newsfeed.user.dto.response.UserResponseDto;
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
    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(
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

    // 회원 정보 수정
    @PatchMapping
    public ResponseEntity<UserResponseDto> updateUser(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestBody @Valid UserRequestDto requestDto
    ) {
        requestDto.setId(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(requestDto));
    }

    // 비밀번호 수정
    @PatchMapping("/password")
    public ResponseEntity<UserResponseDto> updatePassword(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestBody @Valid UserRequestDto requestDto
    ) {
        requestDto.setId(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(userService.updatePassword(requestDto));
    }

    // 회원 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }
}
