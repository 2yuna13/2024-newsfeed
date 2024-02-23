package com.hanghae.newsfeed.admin.service;

import com.hanghae.newsfeed.admin.dto.request.AdminUserRequest;
import com.hanghae.newsfeed.admin.dto.response.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    /**
     * 전체 회원 목록 조회
     * @param nickname 닉네임 검색 정보
     * @param pageable 페이징 정보
     * @return 회원 목록
     */
    Page<AdminUserResponse> getAllUsers(String nickname, Pageable pageable);

    /**
     * 회원 권한 수정
     * @param userId 사용자 아이디
     * @param request 회원 권한 수정 요청
     * @return 수정된 회원 정보
     */
    AdminUserResponse updateUserRoleAndStatus(Long userId, AdminUserRequest request);
}
