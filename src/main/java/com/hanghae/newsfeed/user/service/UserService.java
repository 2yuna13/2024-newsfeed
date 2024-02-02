package com.hanghae.newsfeed.user.service;

import com.hanghae.newsfeed.user.dto.request.SignupRequestDto;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public SignupRequestDto signup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String nickname = requestDto.getNickname();

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
                requestDto.getPassword() // 암호화된 비밀번호로 추후 변경 예정
        );

        // 유저 엔티티를 DB로 저장
        User created = userRepository.save(user);
        // DTO로 변경하여 반환
        return SignupRequestDto.createUserDto(created);
    }
}
