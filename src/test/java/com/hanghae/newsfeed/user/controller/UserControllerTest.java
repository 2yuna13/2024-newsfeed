package com.hanghae.newsfeed.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.user.dto.request.PasswordUpdateRequest;
import com.hanghae.newsfeed.user.dto.request.UserUpdateRequest;
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
//import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    private static final String BASE_URL = "/api/users";
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        String email = "1test@test.com";
        String encodedPassword = new BCryptPasswordEncoder().encode("qwe123!@#");
        String nickname = "1test";
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        // 사용자 정보 & 비밀번호 이력 저장
        User user = new User(email, nickname, encodedPassword, UserRoleEnum.USER, true);
        userRepository.save(user);
        user.updatePassword(encodedPassword);
        // 로그인한 사용자 정보 확인할 때 사용
        userDetails = UserDetailsImpl.builder()
                .id(user.getId())
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .authorities(authorities)
                .build();

        // 유저 목록 조회 시 사용할 mock 데이터
        userRepository.save(new User("user1@test.com", "user1", "password", UserRoleEnum.USER, true));
        userRepository.save(new User("user2@test.com", "user2", "password", UserRoleEnum.USER, true));
        userRepository.save(new User("user3@test.com", "user3", "password", UserRoleEnum.USER, true));
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

    @Test
    @Transactional
    @DisplayName("본인이 작성한 게시물 조회 성공 테스트")
    void getPostsByUserId_success() throws Exception {
        // given
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("테스트를 위한 사용자를 찾을 수 없습니다."));
        postRepository.save(new Post(user, "title1", "content1"));
        postRepository.save(new Post(user, "title2", "content2"));
        postRepository.save(new Post(user, "title3", "content3"));

        // then
        MvcResult result = mvc.perform(get(BASE_URL + "/posts")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseJson);

        // 예상 결과와 일치하는지 확인
        assertEquals("title3", jsonNode.get("content").get(0).get("title").asText());
        assertEquals("title2", jsonNode.get("content").get(1).get("title").asText());
        assertEquals("title1", jsonNode.get("content").get(2).get("title").asText());
        assertEquals(3, jsonNode.get("totalElements").asInt());
    }

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
        String duplicatedNickname = "1test";
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
        assertEquals("user1", jsonNode.get("content").get(1).get("nickname").asText());
        assertEquals("user2", jsonNode.get("content").get(2).get("nickname").asText());
        assertEquals("user3", jsonNode.get("content").get(3).get("nickname").asText());
        assertEquals(4, jsonNode.get("totalElements").asInt());
    }

    @Test
    @Transactional
    @DisplayName("회원 목록 조회 성공 테스트 - 닉네임 검색")
    void getAllUsers_success_search() throws Exception {
        // given
        String searchedNickname = "user";

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
        assertEquals("user1", jsonNode.get("content").get(0).get("nickname").asText());
        assertEquals("user2", jsonNode.get("content").get(1).get("nickname").asText());
        assertEquals("user3", jsonNode.get("content").get(2).get("nickname").asText());
        assertEquals(3, jsonNode.get("totalElements").asInt());
    }
}