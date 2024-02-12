package com.hanghae.newsfeed.auth.controller;

import com.hanghae.newsfeed.auth.dto.response.RefreshTokenDto;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenProvider;
import com.hanghae.newsfeed.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    // access 토큰 재발급
    @PostMapping("/refreshToken")
    public ResponseEntity<RefreshTokenDto> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        RefreshTokenDto accessTokenDto = refreshTokenService.refresh(request);
        String accessToken = accessTokenDto.getAccessToken();
        jwtTokenProvider.accessTokenSetHeader(accessToken, response);
        return ResponseEntity.status(HttpStatus.OK).body(accessTokenDto);
    }
}
