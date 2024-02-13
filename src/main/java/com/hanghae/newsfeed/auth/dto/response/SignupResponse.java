package com.hanghae.newsfeed.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponse {
    private Long id;
    private String email;
    private String msg;
}
