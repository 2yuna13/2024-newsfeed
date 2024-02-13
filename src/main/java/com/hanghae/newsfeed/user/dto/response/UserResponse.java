package com.hanghae.newsfeed.user.dto.response;

import com.hanghae.newsfeed.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String email;
    private String nickname;
    private String description;
    private String profileImage;
    private String msg;

    public static UserResponse createUserDto(User user, String msg) {
        return new UserResponse(
                user.getEmail(),
                user.getNickname(),
                user.getDescription(),
                user.getProfileImage(),
                msg
        );
    }
}