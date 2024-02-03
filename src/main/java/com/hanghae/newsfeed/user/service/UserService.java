package com.hanghae.newsfeed.user.service;

import com.hanghae.newsfeed.security.jwt.JwtTokenProvider;
import com.hanghae.newsfeed.security.jwt.JwtTokenType;
import com.hanghae.newsfeed.user.dto.request.LoginRequestDto;
import com.hanghae.newsfeed.user.dto.request.SignupRequestDto;
import com.hanghae.newsfeed.user.dto.request.UserRequestDto;
import com.hanghae.newsfeed.user.dto.response.LoginResponseDto;
import com.hanghae.newsfeed.user.dto.response.SignupResponseDto;
import com.hanghae.newsfeed.user.dto.response.UserResponseDto;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
                encodingPassword
        );

        // 유저 엔티티를 DB로 저장
        User createdUser = userRepository.save(user);

        // DTO로 변경하여 반환
        return SignupResponseDto.createUserDto(createdUser, "회원가입 성공");
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        String accessToken = jwtTokenProvider.generate(email, JwtTokenType.ACCESS);
        String refreshToken = jwtTokenProvider.generate(email, JwtTokenType.REFRESH);

        return new LoginResponseDto(accessToken, refreshToken, "로그인 성공");
    }

    @Transactional
    public UserResponseDto update(Long id, UserRequestDto requestDto) {
        // 유저 조회 예외 발생
        User target = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 수정 실패, 등록된 사용자가 없습니다."));

        // 닉네임 중복 확인
        userRepository.findByNickname(requestDto.getNickname()).ifPresent(user -> {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        });

        // 유저 수정
        target.patch(requestDto);

        // DB로 갱신
        User updatedUser = userRepository.save(target);

        // DTO로 변경하여 반환
        return UserResponseDto.createUserDto(updatedUser, "유저 정보 수정 성공");
    }
}
