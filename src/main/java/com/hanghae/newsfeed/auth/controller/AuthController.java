package com.hanghae.newsfeed.auth.controller;

import com.hanghae.newsfeed.auth.dto.request.LoginRequestDto;
import com.hanghae.newsfeed.auth.dto.request.SignupRequestDto;
import com.hanghae.newsfeed.auth.dto.response.LoginResponseDto;
import com.hanghae.newsfeed.auth.dto.response.SignupResponseDto;
import com.hanghae.newsfeed.auth.service.AuthService;
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
    private final AuthService authService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.signup(requestDto));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(requestDto, response));
    }
}
