package com.hanghae.newsfeed.auth.service;

import com.hanghae.newsfeed.auth.dto.response.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;

public interface RefreshTokenService {
    /**
     * access 토큰 재발급
     *
     * @param request HttpServletRequest
     * @return 토큰 재발급 결과
     */
    RefreshToken refresh(HttpServletRequest request);
}
