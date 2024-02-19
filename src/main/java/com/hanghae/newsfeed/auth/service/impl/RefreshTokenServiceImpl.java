package com.hanghae.newsfeed.auth.service.impl;

import com.hanghae.newsfeed.auth.dto.response.RefreshToken;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenProvider;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenType;
import com.hanghae.newsfeed.auth.service.RefreshTokenService;
import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.common.redis.RedisService;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisService redisService;

    // access 토큰 재발급
    @Override
    @Transactional
    public RefreshToken refresh(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        String email = jwtTokenProvider.getEmail(refreshToken, JwtTokenType.REFRESH);

        String redisRefreshToken = redisService.getValue("RefreshToken:" + email);
        if (!redisService.checkExistsValue(redisRefreshToken)) {
            throw new CustomException(CustomErrorCode.TOKEN_NOT_FOUND);
        }

        if (refreshToken.equals(redisRefreshToken)) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

            String accessToken = jwtTokenProvider.generate(user.getEmail(), user.getRole(), JwtTokenType.ACCESS);

            return new RefreshToken(accessToken, refreshToken, email, user.getRole());
        } else {
            throw new CustomException(CustomErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }
    }
}
