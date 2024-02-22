package com.hanghae.newsfeed.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.dto.request.PasswordUpdateRequest;
import com.hanghae.newsfeed.user.dto.request.UserUpdateRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private static final String BASE_URL = "/api/users";
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        Long id = 4L;
        String email = "test4@test.com";
        String password = "qwe123!@#";
        String nickname = "test4";
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

        userDetails = UserDetailsImpl.builder()
                .id(id)
                .email(email)
                .password(password)
                .nickname(nickname)
                .authorities(authorities)
                .build();
    }

    @Test
    @Transactional
    @DisplayName("회원 정보 조회 성공 테스트")
    void getUser_success() throws Exception {
        // then
        mvc.perform(get(BASE_URL + "/profile")
                        .with(user(userDetails)) // 사용자 정보를 시뮬레이션하기 위해 MockMvc의 user() 메소드 사용
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userDetails.getEmail()))
                .andExpect(jsonPath("$.nickname").value(userDetails.getUsername()))
                .andExpect(jsonPath("$.msg").value("유저 정보 조회 성공"));
    }

//    @Test
//    @Transactional
//    @DisplayName("본인이 작성한 게시물 조회 성공 테스트")
//    void getPostsByUserId() {
//    }

    @Test
    @Transactional
    @DisplayName("회원 정보 수정 성공 테스트")
    void updateUser_success() throws Exception {
        // given
        String nickname = "test1234";
        String description = "test";

        // when
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(nickname, description);

        // then
        mvc.perform(patch(BASE_URL).with(user(userDetails))
                .content(mapper.writeValueAsString(userUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userDetails.getEmail()))
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.msg").value("유저 정보 수정 성공"));
    }

    @Test
    @Transactional
    @DisplayName("회원 정보 수정 실패 테스트 - 닉네임 형식")
    void updateUser_fail_nickname_valid() throws Exception {
        // given
        String invalidNickname = "테스트";
        String description = "test";

        // when
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(invalidNickname, description);

        // then
        mvc.perform(patch(BASE_URL).with(user(userDetails))
                        .content(mapper.writeValueAsString(userUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nickname").value("알파벳 소문자와 숫자로 이루어진 4자 ~ 10자로 입력해주세요."));
    }

    @Test
    @Transactional
    @DisplayName("회원 정보 수정 실패 테스트 - 닉네임 중복")
    void updateUser_fail_nickname_duplicate() throws Exception {
        // given
        String duplicatedNickname = "test";
        String description = "test";

        // when
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(duplicatedNickname, description);

        // then
        mvc.perform(patch(BASE_URL).with(user(userDetails))
                        .content(mapper.writeValueAsString(userUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("중복된 닉네임이 존재합니다."));
    }

//    @Test
//    @Transactional
//    @DisplayName("프로필 사진 수정 성공 테스트")
//    void updateProfileImage() {
//    }

    @Test
    @Transactional
    @DisplayName("비밀번호 수정 성공 테스트")
    void updatePassword_success() throws Exception {
        // given
        String password = "qwe123!@#";
        String newPassword = "qwe123!@#!";

        // when
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest(password, newPassword);

        // then
        mvc.perform(patch(BASE_URL + "/password")
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(passwordUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userDetails.getEmail()))
                .andExpect(jsonPath("$.nickname").value(userDetails.getUsername()))
                .andExpect(jsonPath("$.msg").value("비밀번호 수정 성공"));

    }

    @Test
    @Transactional
    @DisplayName("비밀번호 수정 실패 테스트 - 비밀번호 형식")
    void updatePassword_fail_valid() throws Exception {
        // given
        String password = "qwe123!@#";
        String newPassword = "qwe123";

        // when
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest(password, newPassword);

        // then
        mvc.perform(patch(BASE_URL + "/password")
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(passwordUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.newPassword").value("비밀번호는 알파멧 대소문자, 숫자, 특수문자가 적어도 1개 이상씩 포함된 8자 ~ 15자로 입력해주세요."));
    }

    @Test
    @Transactional
    @DisplayName("비밀번호 수정 실패 테스트 - 비밀번호 불일치")
    void updatePassword_fail_mismatch() throws Exception {
        // given
        String password = "qwe123!@";
        String newPassword = "qwe123!@#";

        // when
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest(password, newPassword);

        // then
        mvc.perform(patch(BASE_URL + "/password")
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(passwordUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @Transactional
    @DisplayName("비밀번호 수정 실패 테스트 - 비밀번호 중복")
    void updatePassword_fail_duplicate() throws Exception {
        // given
        String password = "qwe123!@#";
        String newPassword = "qwe123!@#";

        // when
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest(password, newPassword);

        // then
        mvc.perform(patch(BASE_URL + "/password")
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(passwordUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("최근에 사용한 비밀번호와 중복되어 사용할 수 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("회원 목록 조회 성공 테스트")
    void getAllUsers_success() throws Exception {
        // then
        MvcResult result = mvc.perform(get(BASE_URL).with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // 회원 목록 조회 결과를 JSON으로 파싱
        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseJson);

        // 예상 결과와 일치하는지 확인
        assertEquals("1test", jsonNode.get("content").get(0).get("nickname").asText());
    }

    @Test
    @Transactional
    @DisplayName("회원 목록 조회 성공 테스트 - 닉네임 검색")
    void getAllUsers_success_search() throws Exception {
        // given
        String searchedNickname = "test";

        // then
        MvcResult result = mvc.perform(get(BASE_URL).with(user(userDetails))
                        .param("nickname", searchedNickname)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // 회원 목록 조회 결과를 JSON으로 파싱
        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseJson);

        // 예상 결과와 일치하는지 확인
        assertEquals("1test", jsonNode.get("content").get(0).get("nickname").asText());
    }
}