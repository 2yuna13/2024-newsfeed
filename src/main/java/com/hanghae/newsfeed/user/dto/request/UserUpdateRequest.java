package com.hanghae.newsfeed.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserUpdateRequest {
    @Pattern(regexp= "^[a-z0-9]{4,10}$", message = "알파벳 소문자와 숫자로 이루어진 4자 ~ 10자로 입력해주세요.")
    private String nickname;
    private String description;
    private String profileImage;
}