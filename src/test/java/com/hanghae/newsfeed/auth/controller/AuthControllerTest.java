package com.hanghae.newsfeed.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.newsfeed.auth.dto.request.LoginRequest;
import com.hanghae.newsfeed.auth.dto.request.SignupRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private static final String BASE_URL = "/api/auth";

    @Test
    @Transactional
    @DisplayName("회원 가입 성공 테스트")
    void signup_success() throws Exception {
        // given
        String email = "test100@test.com";
        String nickname = "test100";
        String password = "qwe123!@#";

        // when
        SignupRequest signupRequest = new SignupRequest(email, nickname, password);

        // then
        mvc.perform(post(BASE_URL + "/signup")
                        .content(mapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.msg").value("회원가입 성공"));
    }

    @Test
    @Transactional
    @DisplayName("회원 가입 실패 테스트 - 이메일 형식")
    void signup_fail_email_valid() throws Exception {
        // given
        String invalidEmail = "test";
        String nickname = "test100";
        String password = "qwe123!@#";

        // when
        SignupRequest signupRequest = new SignupRequest(invalidEmail, nickname, password);

        // then
        mvc.perform(post(BASE_URL + "/signup")
                        .content(mapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("이메일 형식에 맞지 않습니다."));
    }

    @Test
    @Transactional
    @DisplayName("회원 가입 실패 테스트 - 닉네임 형식")
    void signup_fail_nickname_valid() throws Exception {
        // given
        String email = "test100@test.com";
        String invalidNickname = "테스트";
        String password = "qwe123!@#";

        // when
        SignupRequest signupRequest = new SignupRequest(email, invalidNickname, password);

        // then
        mvc.perform(post(BASE_URL + "/signup")
                        .content(mapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nickname").value("알파벳 소문자와 숫자로 이루어진 4자 ~ 10자로 입력해주세요."));
    }

    @Test
    @Transactional
    @DisplayName("회원 가입 실패 테스트 - 비밀번호 형식")
    void signup_fail_password_valid() throws Exception {
        // given
        String email = "test100@test.com";
        String nickname = "test100";
        String invalidPassword = "qwe123";

        // when
        SignupRequest signupRequest = new SignupRequest(email, nickname, invalidPassword);

        // then
        mvc.perform(post(BASE_URL + "/signup")
                        .content(mapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("비밀번호는 알파멧 대소문자, 숫자, 특수문자가 적어도 1개 이상씩 포함된 8자 ~ 15자로 입력해주세요."));
    }

    @Test
    @Transactional
    @DisplayName("회원 가입 실패 테스트 - 이메일 중복")
    void signup_fail_email_duplicate() throws Exception {
        // given
        String duplicatedEmail = "test@test.com";
        String nickname = "test100";
        String password = "qwe123!@#";

        // when
        SignupRequest signupRequest = new SignupRequest(duplicatedEmail, nickname, password);

        // then
        mvc.perform(post(BASE_URL + "/signup")
                        .content(mapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("중복된 이메일이 존재합니다."));
    }

    @Test
    @Transactional
    @DisplayName("회원 가입 실패 테스트 - 닉네임 중복")
    void signup_fail_nickname_duplicate() throws Exception {
        // given
        String email = "test100@test.com";
        String duplicatedNickname = "test";
        String password = "qwe123!@#";

        // when
        SignupRequest signupRequest = new SignupRequest(email, duplicatedNickname, password);

        // then
        mvc.perform(post(BASE_URL + "/signup")
                        .content(mapper.writeValueAsString(signupRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("중복된 닉네임이 존재합니다."));
    }

    @Test
    @Transactional
    @DisplayName("로그인 성공 테스트")
    void login_success() throws Exception {
        // given
        String email = "test@test.com";
        String password = "qwe123!@#";

        // when
        LoginRequest loginRequest = new LoginRequest(email, password);

        // then
        mvc.perform(post(BASE_URL + "/login")
                        .content(mapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("로그인 성공"));
    }

    @Test
    @Transactional
    @DisplayName("로그인 실패 테스트 - 이메일 형식")
    void login_fail_email_valid() throws Exception {
        // given
        String invalidEmail = "test";
        String password = "qwe123!@#";

        // when
        LoginRequest loginRequest = new LoginRequest(invalidEmail, password);

        // then
        mvc.perform(post(BASE_URL + "/login")
                        .content(mapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("이메일 형식에 맞지 않습니다."));
    }

    @Test
    @Transactional
    @DisplayName("로그인 실패 테스트 - 비밀번호 형식")
    void login_fail_password_valid() throws Exception {
        // given
        String email = "test@test.com";
        String invalidPassword = "qwe123";

        // when
        LoginRequest loginRequest = new LoginRequest(email, invalidPassword);

        // then
        mvc.perform(post(BASE_URL + "/login")
                        .content(mapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("비밀번호는 알파멧 대소문자, 숫자, 특수문자가 적어도 1개 이상씩 포함된 8자 ~ 15자로 입력해주세요."));
    }

    @Test
    @Transactional
    @DisplayName("로그인 실패 테스트 - 존재하지 않는 이메일")
    void login_fail_email_not_exist() throws Exception {
        // given
        String nonExistEmail = "test1234@test.com";
        String password = "qwe123!@#";

        // when
        LoginRequest loginRequest = new LoginRequest(nonExistEmail, password);

        // then
        mvc.perform(post(BASE_URL + "/login")
                        .content(mapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 사용자가 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void login_fail_password_mismatch() throws Exception {
        // given
        String email = "test@test.com";
        String wrongPassword = "qwe123!@";

        // when
        LoginRequest loginRequest = new LoginRequest(email, wrongPassword);

        // then
        mvc.perform(post(BASE_URL + "/login")
                        .content(mapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @Transactional
    @DisplayName("로그인 실패 테스트 - 탈퇴된 사용자")
    void login_fail_user_deactivated() throws Exception {
        // given
        String deactivatedUserEmail = "test3@test.com";
        String wrongPassword = "qwe123!@#";

        // when
        LoginRequest loginRequest = new LoginRequest(deactivatedUserEmail, wrongPassword);

        // then
        mvc.perform(post(BASE_URL + "/login")
                        .content(mapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("해당 계정은 탈퇴되었습니다."));
    }
}