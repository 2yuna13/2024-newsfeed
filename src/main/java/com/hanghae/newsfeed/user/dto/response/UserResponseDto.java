package com.hanghae.newsfeed.user.dto.response;

import com.hanghae.newsfeed.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String description;
    private String profileImage;
    private Boolean active;
    private String msg;

    public static UserResponseDto createUserDto(User user, String msg) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getDescription(),
                user.getProfileImage(),
                user.getActive(),
                msg
        );
    }
}