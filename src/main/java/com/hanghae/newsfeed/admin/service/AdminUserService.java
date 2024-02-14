package com.hanghae.newsfeed.admin.service;

import com.hanghae.newsfeed.admin.dto.request.AdminUserRequest;
import com.hanghae.newsfeed.admin.dto.response.AdminUserResponse;
import com.hanghae.newsfeed.common.exception.HttpException;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import com.hanghae.newsfeed.user.type.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;

    // 전체 회원 목록 조회
    public List<AdminUserResponse> getAllUsers() {
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .map(user -> AdminUserResponse.createAdminUserDto(user, "전체 회원 조회 성공"))
                .collect(Collectors.toList());
    }

    // 회원 권한 수정(USER -> ADMIN / active = false)
    public AdminUserResponse updateUserRoleAndStatus(Long userId, AdminUserRequest request) {
        // 유저 조회 예외 발생
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        // 이미 탈퇴한 회원은 수정 불가능
        if (!target.getActive()) {
            throw new HttpException(false, "이미 탈퇴한 회원입니다.", HttpStatus.BAD_REQUEST);
        }

        // 관리자의 role과 active 수정 불가능
        if (target.getRole() == UserRoleEnum.ADMIN) {
            throw new HttpException(false, "관리자의 권한은 수정할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 유저 수정
        target.updateUserRoleAndActive(request);

        // DB로 갱신
        User updatedUser = userRepository.save(target);

        // DTO로 변경하여 반환
        return AdminUserResponse.createAdminUserDto(updatedUser, "회원 권한 수정 성공");
    }
}
