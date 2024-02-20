package com.hanghae.newsfeed.auth.dto.response;

import com.hanghae.newsfeed.user.type.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    private String accessToken;
    private String refreshToken;
    private String email;
    private UserRoleEnum role;
}
