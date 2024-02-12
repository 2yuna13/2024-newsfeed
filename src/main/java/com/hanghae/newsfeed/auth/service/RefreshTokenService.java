package com.hanghae.newsfeed.auth.service;

import com.hanghae.newsfeed.auth.dto.response.RefreshTokenDto;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenProvider;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenType;
import com.hanghae.newsfeed.common.redis.RedisService;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisService redisService;

    // access 토큰 재발급
    @Transactional
    public RefreshTokenDto refresh(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("refresh 토큰이 없습니다.");
        }

        String email = jwtTokenProvider.getEmail(refreshToken, JwtTokenType.REFRESH);

        String redisRefreshToken = redisService.getValue("RefreshToken:" + email);
        if (!redisService.checkExistsValue(redisRefreshToken)) {
            throw new IllegalArgumentException("refresh 토큰이 유효하지 않습니다.");
        }

        if (refreshToken.equals(redisRefreshToken)) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

            String accessToken = jwtTokenProvider.generate(user.getEmail(), user.getRole(), JwtTokenType.ACCESS);

            return new RefreshTokenDto(accessToken, refreshToken, email, user.getRole());
        } else {
            throw new IllegalArgumentException("refresh 토큰이 일치하지 않습니다.");
        }
    }
}
