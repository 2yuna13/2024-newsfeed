package com.hanghae.newsfeed.auth.service;

import com.hanghae.newsfeed.auth.dto.request.LoginRequest;
import com.hanghae.newsfeed.auth.dto.request.SignupRequest;
import com.hanghae.newsfeed.auth.dto.response.LoginResponse;
import com.hanghae.newsfeed.auth.dto.response.LogoutResponse;
import com.hanghae.newsfeed.auth.dto.response.SignupResponse;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenProvider;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenType;
import com.hanghae.newsfeed.common.redis.RedisService;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import com.hanghae.newsfeed.user.type.UserRoleEnum;
import com.hanghae.newsfeed.common.exception.HttpException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisService redisService;

    // 회원가입
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String email = request.getEmail();
        String nickname = request.getNickname();
        String encodingPassword = bCryptPasswordEncoder.encode(request.getPassword());

        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)){
            throw new HttpException(false, "중복된 이메일이 존재합니다.", HttpStatus.BAD_REQUEST);
        };

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(nickname)) {
            throw new HttpException(false, "중복된 닉네임이 존재합니다.", HttpStatus.BAD_REQUEST);
        }

        // 유저 엔티티 생성
        User user = new User(
                email,
                nickname,
                encodingPassword,
                UserRoleEnum.USER
        );

        // 비밀번호 이력 저장
        user.updatePassword(encodingPassword);

        // 유저 엔티티를 DB로 저장
        userRepository.save(user);

        // DTO로 변경하여 반환
        return new SignupResponse(user.getId(), email, "회원가입 성공");
    }

    // 로그인
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        String email = request.getEmail();
        String password = request.getPassword();

        // 사용자 확인
        User user = userRepository.findByEmail(email).orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new HttpException(false, "비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST);
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
    @Transactional
    public LogoutResponse logout(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);

        if (redisService.keyExists(accessToken)) {
            throw new HttpException(false, "이미 로그아웃한 사용자입니다.", HttpStatus.BAD_REQUEST);
        }

        if (!jwtTokenProvider.isExpired(accessToken, JwtTokenType.ACCESS)) {
            throw new HttpException(false, "토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
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
