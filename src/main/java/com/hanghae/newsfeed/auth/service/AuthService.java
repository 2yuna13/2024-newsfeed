package com.hanghae.newsfeed.auth.service;

import com.hanghae.newsfeed.auth.dto.request.LoginRequest;
import com.hanghae.newsfeed.auth.dto.request.SignupRequest;
import com.hanghae.newsfeed.auth.dto.response.LoginResponse;
import com.hanghae.newsfeed.auth.dto.response.LogoutResponse;
import com.hanghae.newsfeed.auth.dto.response.SignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    /**
     * 회원가입
     * @param request 회원가입 요청
     * @return 회원가입 결과
     */
    SignupResponse signup(SignupRequest request);

    /**
     * 로그인
     * @param request  로그인 요청
     * @param response HttpServletResponse
     * @return 로그인 결과
     */
    LoginResponse login(LoginRequest request, HttpServletResponse response);

    /**
     * 로그아웃
     * @param request HttpServletRequest
     * @return 로그아웃 결과
     */
    LogoutResponse logout(HttpServletRequest request);
}
