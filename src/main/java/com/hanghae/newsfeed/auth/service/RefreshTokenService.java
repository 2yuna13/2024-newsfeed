package com.hanghae.newsfeed.auth.service;

import com.hanghae.newsfeed.auth.dto.response.RefreshToken;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenProvider;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenType;
import com.hanghae.newsfeed.common.exception.HttpException;
import com.hanghae.newsfeed.common.redis.RedisService;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisService redisService;

    // access 토큰 재발급
    @Transactional
    public RefreshToken refresh(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        String email = jwtTokenProvider.getEmail(refreshToken, JwtTokenType.REFRESH);

        String redisRefreshToken = redisService.getValue("RefreshToken:" + email);
        if (!redisService.checkExistsValue(redisRefreshToken)) {
            throw new HttpException(false, "토큰이 유효하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        if (refreshToken.equals(redisRefreshToken)) {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

            String accessToken = jwtTokenProvider.generate(user.getEmail(), user.getRole(), JwtTokenType.ACCESS);

            return new RefreshToken(accessToken, refreshToken, email, user.getRole());
        } else {
            throw new HttpException(false, "Refresh 토큰이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
