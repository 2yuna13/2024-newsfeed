package com.hanghae.newsfeed.admin.controller;

import com.hanghae.newsfeed.user.dto.request.UserRequestDto;
import com.hanghae.newsfeed.user.dto.response.UserResponseDto;
import com.hanghae.newsfeed.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admins")
public class AdminController {
    private final UserService userService;

    // 회원 목록 조회
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    // 회원 권한 수정(USER -> ADMIN / active = false)
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUserRoleAndStatus(
            @PathVariable Long userId,
            @RequestBody UserRequestDto requestDto
    ) {
        requestDto.setId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserRoleAndStatus(requestDto));
    }
}
