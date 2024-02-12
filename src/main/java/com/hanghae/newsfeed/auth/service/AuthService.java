package com.hanghae.newsfeed.auth.service;

import com.hanghae.newsfeed.auth.dto.request.LoginRequestDto;
import com.hanghae.newsfeed.auth.dto.request.SignupRequestDto;
import com.hanghae.newsfeed.auth.dto.response.LoginResponseDto;
import com.hanghae.newsfeed.auth.dto.response.SignupResponseDto;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenProvider;
import com.hanghae.newsfeed.auth.security.jwt.JwtTokenType;
import com.hanghae.newsfeed.common.redis.RedisService;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import com.hanghae.newsfeed.user.type.UserRoleEnum;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();
        String encodingPassword = bCryptPasswordEncoder.encode(requestDto.getPassword());

        // 이메일 중복 확인
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
        });

        // 닉네임 중복 확인
        userRepository.findByNickname(nickname).ifPresent(user -> {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        });

        // 유저 엔티티 생성
        User user = new User(
                email,
                nickname,
                encodingPassword,
                UserRoleEnum.USER
        );

        // 비밀번호 이력 저장
        user.patchPassword(encodingPassword);

        // 유저 엔티티를 DB로 저장
        User createdUser = userRepository.save(user);

        // DTO로 변경하여 반환
        return SignupResponseDto.createUserDto(createdUser, "회원가입 성공");
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto requestDto, HttpServletResponse response) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        UserRoleEnum role = user.getRole();
        String accessToken = jwtTokenProvider.generate(email, role, JwtTokenType.ACCESS);
        String refreshToken = jwtTokenProvider.generate(email, role, JwtTokenType.REFRESH);

        Long expiredTime = jwtTokenProvider.getExpiredTime(refreshToken, JwtTokenType.REFRESH);
        redisService.setValues("RefreshToken:" + user.getEmail(), refreshToken, expiredTime, TimeUnit.MILLISECONDS);

        jwtTokenProvider.accessTokenSetHeader(accessToken, response);
        jwtTokenProvider.refreshTokenSetHeader(refreshToken, response);

        return new LoginResponseDto(accessToken, refreshToken, "로그인 성공");
    }
}
