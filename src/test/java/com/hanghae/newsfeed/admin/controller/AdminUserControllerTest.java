package com.hanghae.newsfeed.admin.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.newsfeed.admin.dto.request.AdminUserRequest;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import com.hanghae.newsfeed.user.type.UserRoleEnum;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
class AdminUserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    private static final String BASE_URL = "/api/admins";
    private UserDetailsImpl userDetails;
    private User activateUser;
    private User deactivateUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        String email = "admin@test.com";
        String encodedPassword = new BCryptPasswordEncoder().encode("qwe123!@#");
        String nickname = "admin";
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        User admin = userRepository.save(new User(email, nickname, encodedPassword, UserRoleEnum.ADMIN, true));
        userDetails = UserDetailsImpl.builder()
                .id(admin.getId())
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .authorities(authorities)
                .build();
        // 탈퇴하지 않은 일반 사용자
        activateUser = userRepository.save(new User("user1@test.com", "user1", "password", UserRoleEnum.USER, true));
        // 탈퇴한 사용자
        deactivateUser = userRepository.save(new User("user2@test.com", "user2", "password", UserRoleEnum.USER, false));
        // 관리자
        adminUser = userRepository.save(new User("user3@test.com", "user3", "password", UserRoleEnum.ADMIN, true));
    }

    @Test
    @Transactional
    @DisplayName("전체 회원 목록 조회 성공 테스트")
    void getAllUsers_success() throws Exception {
        // then
        MvcResult result = mvc.perform(get(BASE_URL + "/users")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // 회원 목록 조회 결과를 JSON으로 파싱
        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseJson);

        // 예상 결과와 일치하는지 확인
        assertEquals("admin", jsonNode.get("content").get(0).get("nickname").asText());
        assertEquals("user1", jsonNode.get("content").get(1).get("nickname").asText());
        assertEquals("user2", jsonNode.get("content").get(2).get("nickname").asText());
        assertEquals("user3", jsonNode.get("content").get(3).get("nickname").asText());
        assertEquals(4, jsonNode.get("totalElements").asInt());
    }

    @Test
    @Transactional
    @DisplayName("전체 회원 목록 조회 성공 테스트 - 닉네임 검색")
    void getAllUsers_success_search() throws Exception {
        // given
        String searchedNickname = "user";

        // then
        MvcResult result = mvc.perform(get(BASE_URL + "/users")
                        .with(user(userDetails))
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
        assertEquals("user1", jsonNode.get("content").get(0).get("nickname").asText());
        assertEquals("user2", jsonNode.get("content").get(1).get("nickname").asText());
        assertEquals("user3", jsonNode.get("content").get(2).get("nickname").asText());
        assertEquals(3, jsonNode.get("totalElements").asInt());
    }

    @Test
    @Transactional
    @DisplayName("회원 정보 수정 성공 테스트 - 권한 수정")
    void updateUserRoleAndStatus_success_role() throws Exception {
        // given, when
        AdminUserRequest adminUserRequest = new AdminUserRequest();
        adminUserRequest.setRole(UserRoleEnum.ADMIN);

        // then
        mvc.perform(patch(BASE_URL + "/" + activateUser.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(adminUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(activateUser.getEmail()))
                .andExpect(jsonPath("$.nickname").value(activateUser.getNickname()))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.msg").value("회원 권한 수정 성공"));
    }

    @Test
    @Transactional
    @DisplayName("회원 정보 수정 성공 테스트 - 비활성화")
    void updateUserRoleAndStatus_success_active() throws Exception {
        // given, when
        AdminUserRequest adminUserRequest = new AdminUserRequest();
        adminUserRequest.setActive(false);

        // then
        mvc.perform(patch(BASE_URL + "/" + activateUser.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(adminUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(activateUser.getEmail()))
                .andExpect(jsonPath("$.nickname").value(activateUser.getNickname()))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.msg").value("회원 권한 수정 성공"));
    }

    @Test
    @Transactional
    @DisplayName("회원 정보 수정 실패 테스트 - 존재하지 않는 사용자")
    void updateUserRoleAndStatus_fail_user_not_exist() throws Exception {
        // given
        long nonExistentUserId = 999L;
        UserRoleEnum role = UserRoleEnum.ADMIN;
        Boolean active = true;

        // when
        AdminUserRequest adminUserRequest = new AdminUserRequest(role, active);

        // then
        mvc.perform(patch(BASE_URL + "/" + nonExistentUserId)
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(adminUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 사용자가 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("회원 정보 수정 실패 테스트 - 이미 탈퇴한 사용자")
    void updateUserRoleAndStatus_fail_user_deactivated() throws Exception {
        // given
        UserRoleEnum role = UserRoleEnum.ADMIN;
        Boolean active = true;

        // when
        AdminUserRequest adminUserRequest = new AdminUserRequest(role, active);

        // then
        mvc.perform(patch(BASE_URL + "/" + deactivateUser.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(adminUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("해당 계정은 탈퇴되었습니다."));
    }

    @Test
    @Transactional
    @DisplayName("회원 정보 수정 실패 테스트 - 관리자")
    void updateUserRoleAndStatus_fail_admin() throws Exception {
        // given
        UserRoleEnum role = UserRoleEnum.ADMIN;
        Boolean active = true;

        // when
        AdminUserRequest adminUserRequest = new AdminUserRequest(role, active);

        // then
        mvc.perform(patch(BASE_URL + "/" + adminUser.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(adminUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("관리자의 권한은 수정할 수 없습니다."));
    }
}