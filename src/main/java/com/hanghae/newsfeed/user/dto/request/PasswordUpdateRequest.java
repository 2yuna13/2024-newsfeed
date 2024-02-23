package com.hanghae.newsfeed.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordUpdateRequest {
    private String password;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,15}$", message = "비밀번호는 알파멧 대소문자, 숫자, 특수문자가 적어도 1개 이상씩 포함된 8자 ~ 15자로 입력해주세요.")
    private String newPassword;
}
