package com.hanghae.newsfeed.auth.dto.response;

import com.hanghae.newsfeed.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String msg;

    public static SignupResponseDto createUserDto(User user, String msg) {
        return new SignupResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                msg
        );
    }
}
