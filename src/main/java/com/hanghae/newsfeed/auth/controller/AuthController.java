package com.hanghae.newsfeed.auth.controller;

import com.hanghae.newsfeed.auth.dto.request.LoginRequest;
import com.hanghae.newsfeed.auth.dto.request.SignupRequest;
import com.hanghae.newsfeed.auth.dto.response.LoginResponse;
import com.hanghae.newsfeed.auth.dto.response.LogoutResponse;
import com.hanghae.newsfeed.auth.dto.response.SignupResponse;
import com.hanghae.newsfeed.auth.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServiceImpl authService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request, response));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.logout(request));
    }
}
