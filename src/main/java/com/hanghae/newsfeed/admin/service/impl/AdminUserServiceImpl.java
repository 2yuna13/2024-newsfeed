package com.hanghae.newsfeed.admin.service.impl;

import com.hanghae.newsfeed.admin.dto.request.AdminUserRequest;
import com.hanghae.newsfeed.admin.dto.response.AdminUserResponse;
import com.hanghae.newsfeed.admin.service.AdminUserService;
import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import com.hanghae.newsfeed.user.type.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;

    // 전체 회원 목록 조회
    @Override
    public Page<AdminUserResponse> getAllUsers(String nickname, Pageable pageable) {
        Page<User> allUsers;
        if (nickname!= null) {
            allUsers = userRepository.searchByNickname(nickname, null, pageable);
        } else {
            allUsers = userRepository.findAll(pageable);
        }

        return allUsers
                .map(user -> AdminUserResponse.createAdminUserDto(user, "전체 회원 조회 성공"));
    }

    // 회원 권한 수정(USER -> ADMIN / active = false)
    @Override
    public AdminUserResponse updateUserRoleAndStatus(Long userId, AdminUserRequest request) {
        // 유저 조회 예외 발생
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 이미 탈퇴한 회원은 수정 불가능
        if (!target.getActive()) {
            throw new CustomException(CustomErrorCode.USER_DEACTIVATED);
        }

        // 관리자의 role과 active 수정 불가능
        if (target.getRole() == UserRoleEnum.ADMIN) {
            throw new CustomException(CustomErrorCode.ADMIN_CANNOT_BE_MODIFIED);
        }

        // 유저 수정
        target.updateUserRoleAndActive(request);

        // DB로 갱신
        User updatedUser = userRepository.save(target);

        // DTO로 변경하여 반환
        return AdminUserResponse.createAdminUserDto(updatedUser, "회원 권한 수정 성공");
    }
}
