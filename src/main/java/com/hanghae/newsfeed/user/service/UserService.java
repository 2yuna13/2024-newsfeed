package com.hanghae.newsfeed.user.service;

import com.hanghae.newsfeed.post.dto.response.PostResponseDto;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.security.UserDetailsImpl;
import com.hanghae.newsfeed.security.jwt.JwtTokenProvider;
import com.hanghae.newsfeed.security.jwt.JwtTokenType;
import com.hanghae.newsfeed.user.dto.request.LoginRequestDto;
import com.hanghae.newsfeed.user.dto.request.SignupRequestDto;
import com.hanghae.newsfeed.user.dto.request.UserRequestDto;
import com.hanghae.newsfeed.user.dto.response.LoginResponseDto;
import com.hanghae.newsfeed.user.dto.response.SignupResponseDto;
import com.hanghae.newsfeed.user.dto.response.UserResponseDto;
import com.hanghae.newsfeed.user.entity.PwHistory;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.entity.UserRoleEnum;
import com.hanghae.newsfeed.user.repository.PwHistoryRepository;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PwHistoryRepository pwHistoryRepository;

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

        // 추가로 특정 조건을 확인하여 ADMIN 권한 부여 -> 변경 필요
//        if (Objects.equals(nickname, "admin")) {
//            user.setRole(UserRoleEnum.ADMIN);
//        }

        // 비밀번호 이력 저장
        user.patchPassword(encodingPassword);

        // 유저 엔티티를 DB로 저장
        User createdUser = userRepository.save(user);

        // DTO로 변경하여 반환
        return SignupResponseDto.createUserDto(createdUser, "회원가입 성공");
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto requestDto) {
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

        return new LoginResponseDto(accessToken, refreshToken, "로그인 성공");
    }

    // 회원 정보 조회
    public UserResponseDto getUser(UserDetailsImpl userDetails) {
        User user =  userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

        return UserResponseDto.createUserDto(user, "유저 정보 조회 성공");
    }

    // 내가 작성한 게시물 조회
    public List<PostResponseDto> getPostsByUserId(UserDetailsImpl userDetails) {
        User user =  userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

        List<Post> allPosts = postRepository.findByUserId(user.getId());

        return allPosts.stream()
                .map(post -> PostResponseDto.createPostDto(post, "게시물 조회 성공"))
                .collect(Collectors.toList());
    }

    // 회원 정보 수정
    @Transactional
    public UserResponseDto updateUser(UserRequestDto requestDto) {
        // 유저 조회 예외 발생
        User target = userRepository.findById(requestDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 수정 실패, 등록된 사용자가 없습니다."));

        // 닉네임 중복 확인
        userRepository.findByNickname(requestDto.getNickname()).ifPresent(user -> {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다.");
        });

        // 유저 수정
        target.patchUser(requestDto);

        // DB로 갱신
        User updatedUser = userRepository.save(target);

        // DTO로 변경하여 반환
        return UserResponseDto.createUserDto(updatedUser, "유저 정보 수정 성공");
    }

    // 비밀번호 수정
    @Transactional
    public UserResponseDto updatePassword(UserRequestDto requestDto) {
        // 유저 조회 예외 발생
        User target = userRepository.findById(requestDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("비밀번호 수정 실패, 등록된 사용자가 없습니다."));

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(requestDto.getPassword(), target.getPassword())) {
            throw new IllegalArgumentException("비밀번호 수정 실패, 비밀번호가 일치하지 않습니다.");
        }

        // 최신 비밀번호 3개 가져와서 중복 확인
        List<PwHistory> pwHistories = pwHistoryRepository.findTop3ByUserOrderByCreatedAtDesc(target);

        boolean isPasswordDuplicate = pwHistories.stream()
                .anyMatch(pwHistory -> bCryptPasswordEncoder.matches(requestDto.getNewPassword(), pwHistory.getPassword()));

        if (isPasswordDuplicate) {
            throw new IllegalArgumentException("비밀번호 수정 실패, 최근에 사용한 비밀번호와 중복되어 사용할 수 없습니다.");
        }

        // 비밀번호 수정
        String newPassword = bCryptPasswordEncoder.encode(requestDto.getNewPassword());
        target.patchPassword(newPassword);

        User updatedUser = userRepository.save(target);

        return UserResponseDto.createUserDto(updatedUser, "비밀번호 수정 성공");
    }

    // 회원 목록 조회
    public List<UserResponseDto> getAllUsers() {

        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .map(user -> UserResponseDto.createUserDto((user), "유저 조회 성공"))
                .collect(Collectors.toList());
    }
}
