package com.hanghae.newsfeed.user.service;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.user.dto.request.PasswordUpdateRequest;
import com.hanghae.newsfeed.user.dto.request.UserUpdateRequest;
import com.hanghae.newsfeed.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    /**
     * 회원 정보 조회
     * @param userDetails 사용자 상세 정보
     * @return 회원 정보
     */
    UserResponse getUser(UserDetailsImpl userDetails);

    /**
     * 내가 작성한 게시물 조회
     * @param userDetails 사용자 상세 정보
     * @param pageable 페이징 정보
     * @return 사용자가 작성한 게시물 목록
     */
    Page<PostResponse> getPostsByUserId(UserDetailsImpl userDetails, Pageable pageable);

    /**
     * 회원 정보 수정 (닉네임, 소개)
     * @param userDetails 사용자 상세 정보
     * @param request 사용자 정보 수정 요청
     * @return 수정된 회원 정보
     */
    UserResponse updateUser(UserDetailsImpl userDetails, UserUpdateRequest request);

    /**
     * 프로필 사진 수정
     * @param userDetails 사용자 상세 정보
     * @param image 프로필 사진 파일
     * @return 수정된 회원 정보
     */
    UserResponse updateProfileImage(UserDetailsImpl userDetails, MultipartFile image) throws IOException;

    /**
     * 비밀번호 수정
     * @param userDetails 사용자 상세 정보
     * @param request 비밀번호 수정 요청
     * @return 수정된 회원 정보
     */
    UserResponse updatePassword(UserDetailsImpl userDetails, PasswordUpdateRequest request);

    /**
     * 회원 목록 조회
     * @param pageable 페이징 정보
     * @return 활성화된 회원 목록
     */
    Page<UserResponse> getActiveUsers(Pageable pageable);
}
