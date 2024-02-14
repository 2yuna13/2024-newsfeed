package com.hanghae.newsfeed.user.service;

import com.hanghae.newsfeed.common.exception.HttpException;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.dto.request.PasswordUpdateRequest;
import com.hanghae.newsfeed.user.dto.request.UserUpdateRequest;
import com.hanghae.newsfeed.user.dto.response.UserResponse;
import com.hanghae.newsfeed.user.entity.PwHistory;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.PwHistoryRepository;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PwHistoryRepository pwHistoryRepository;

    // 회원 정보 조회
    public UserResponse getUser(UserDetailsImpl userDetails) {
        User user =  userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        return UserResponse.createUserDto(user, "유저 정보 조회 성공");
    }

    // 내가 작성한 게시물 조회
    public List<PostResponse> getPostsByUserId(UserDetailsImpl userDetails) {
        User user =  userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        List<Post> allPosts = postRepository.findByUserId(user.getId());

        return allPosts.stream()
                .map(post -> PostResponse.createPostDto(post, "게시물 조회 성공"))
                .collect(Collectors.toList());
    }

    // 회원 정보 수정 (닉네임, 소개, 프로필 사진)
    @Transactional
    public UserResponse updateUser(UserDetailsImpl userDetails, UserUpdateRequest request) {
        // 유저 조회 예외 발생
        User target = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new HttpException(false, "중복된 닉네임이 존재합니다.", HttpStatus.BAD_REQUEST);
        }

        // 유저 수정
        target.updateUser(request);

        // DB로 갱신
        User updatedUser = userRepository.save(target);

        // DTO로 변경하여 반환
        return UserResponse.createUserDto(updatedUser, "유저 정보 수정 성공");
    }

    // 비밀번호 수정
    @Transactional
    public UserResponse updatePassword(UserDetailsImpl userDetails, PasswordUpdateRequest request) {
        // 유저 조회 예외 발생
        User target = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(request.getPassword(), target.getPassword())) {
            throw new HttpException(false, "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 최신 비밀번호 3개 가져와서 중복 확인
        List<PwHistory> pwHistories = pwHistoryRepository.findTop3ByUserOrderByCreatedAtDesc(target);

        boolean isPasswordDuplicate = pwHistories.stream()
                .anyMatch(pwHistory -> bCryptPasswordEncoder.matches(request.getNewPassword(), pwHistory.getPassword()));

        if (isPasswordDuplicate) {
            throw new HttpException(false, "최근에 사용한 비밀번호와 중복되어 사용할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 비밀번호 수정
        String newPassword = bCryptPasswordEncoder.encode(request.getNewPassword());
        target.updatePassword(newPassword);

        User updatedUser = userRepository.save(target);

        return UserResponse.createUserDto(updatedUser, "비밀번호 수정 성공");
    }

    // 회원 목록 조회
    public List<UserResponse> getActiveUsers() {

        List<User> activeUsers = userRepository.findByActiveTrue();

        return activeUsers.stream()
                .map(user -> UserResponse.createUserDto(user, "유저 조회 성공"))
                .collect(Collectors.toList());
    }
}