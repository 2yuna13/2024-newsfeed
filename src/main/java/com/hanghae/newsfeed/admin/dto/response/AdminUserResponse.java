package com.hanghae.newsfeed.admin.dto.response;

import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.type.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {
    private String email;
    private String nickname;
    private UserRoleEnum role;
    private String description;
    private String profileImage;
    private Boolean active;
    private String msg;

    public static AdminUserResponse createAdminUserDto(User user, String msg) {
        return new AdminUserResponse(
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getDescription(),
                user.getProfileImage(),
                user.getActive(),
                msg
        );
    }
}