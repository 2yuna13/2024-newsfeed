package com.hanghae.newsfeed.admin.service;

import com.hanghae.newsfeed.admin.dto.request.AdminUserRequest;
import com.hanghae.newsfeed.admin.dto.response.AdminUserResponse;

import java.util.List;

public interface AdminUserService {
    /**
     * 전체 회원 목록 조회
     * @return 회원 목록
     */
    List<AdminUserResponse> getAllUsers();

    /**
     * 회원 권한 수정
     * @param userId 사용자 아이디
     * @param request 회원 권한 수정 요청
     * @return 수정된 회원 정보
     */
    AdminUserResponse updateUserRoleAndStatus(Long userId, AdminUserRequest request);
}
