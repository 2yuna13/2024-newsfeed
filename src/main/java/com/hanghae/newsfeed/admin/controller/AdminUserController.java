package com.hanghae.newsfeed.admin.controller;

import com.hanghae.newsfeed.admin.dto.request.AdminUserRequest;
import com.hanghae.newsfeed.admin.dto.response.AdminUserResponse;
import com.hanghae.newsfeed.admin.service.impl.AdminUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admins")
public class AdminUserController {
    private final AdminUserServiceImpl adminUserService;

    // 회원 목록 조회
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(adminUserService.getAllUsers());
    }

    // 회원 권한 수정(USER -> ADMIN / active = false)
    @PatchMapping("/{userId}")
    public ResponseEntity<AdminUserResponse> updateUserRoleAndStatus(
            @PathVariable Long userId,
            @RequestBody AdminUserRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminUserService.updateUserRoleAndStatus(userId, request));
    }
}
