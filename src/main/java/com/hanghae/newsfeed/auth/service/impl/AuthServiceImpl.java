package com.hanghae.newsfeed.auth.service.impl;

import com.hanghae.newsfeed.auth.dto.request.LoginRequest;
import com.hanghae.newsfeed.auth.dto.request.SignupRequest;
import com.hanghae.newsfeed.auth.dto.response.LoginResponse;
import com.hanghae.newsfeed.auth.dto.response.LogoutResponse;
import com.hanghae.newsfeed.auth.dto.response.SignupResponse;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenProvider;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenType;
import com.hanghae.newsfeed.auth.service.AuthService;
import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.redis.RedisService;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import com.hanghae.newsfeed.user.type.UserRoleEnum;
import com.hanghae.newsfeed.common.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisService redisService;

    // 회원가입
    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String email = request.getEmail();
        String nickname = request.getNickname();
        String encodingPassword = bCryptPasswordEncoder.encode(request.getPassword());

        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)){
            throw new CustomException(CustomErrorCode.DUPLICATED_EMAIL);
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(CustomErrorCode.DUPLICATED_NICKNAME);
        }

        // 유저 엔티티 생성
        User user = new User(
                email,
                nickname,
                encodingPassword,
                UserRoleEnum.USER,
                true
        );

        // 비밀번호 이력 저장
        user.updatePassword(encodingPassword);

        // 유저 엔티티를 DB로 저장
        userRepository.save(user);

        // DTO로 변경하여 반환
        return new SignupResponse(user.getId(), email, "회원가입 성공");
    }

    // 로그인
    @Override
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        String email = request.getEmail();
        String password = request.getPassword();

        // 사용자 확인
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(CustomErrorCode.PASSWORD_NOT_MATCH);
        }

        // 탈퇴한 사용자인지 확인
        if (!user.getActive()) {
            throw new CustomException(CustomErrorCode.USER_DEACTIVATED);
        }

        UserRoleEnum role = user.getRole();
        String accessToken = jwtTokenProvider.generate(email, role, JwtTokenType.ACCESS);
        String refreshToken = jwtTokenProvider.generate(email, role, JwtTokenType.REFRESH);

        Long expiredTime = jwtTokenProvider.getExpiredTime(refreshToken, JwtTokenType.REFRESH);
        redisService.setValues("RefreshToken:" + user.getEmail(), refreshToken, expiredTime, TimeUnit.MILLISECONDS);

        jwtTokenProvider.accessTokenSetHeader(accessToken, response);
        jwtTokenProvider.refreshTokenSetHeader(refreshToken, response);

        return new LoginResponse(accessToken, refreshToken, "로그인 성공");
    }

    // 로그아웃
    @Override
    @Transactional
    public LogoutResponse logout(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);

        if (redisService.keyExists(accessToken)) {
            throw new CustomException(CustomErrorCode.ALREADY_LOGOUT);
        }

        if (!jwtTokenProvider.isExpired(accessToken, JwtTokenType.ACCESS)) {
            throw new CustomException(CustomErrorCode.TOKEN_EXPIRED);
        }

        String email = jwtTokenProvider.getEmail(accessToken, JwtTokenType.ACCESS);

        String redisKey = "RefreshToken:" + email;
        if (redisService.getValue(redisKey) != null) {
            redisService.deleteKey(redisKey);
        }

        Long expiredTime = jwtTokenProvider.getExpiredTime(accessToken, JwtTokenType.ACCESS);
        redisService.setValues(accessToken, "", expiredTime, TimeUnit.MILLISECONDS);

        return new LogoutResponse(email, "로그아웃 성공");
    }
}
