package com.hanghae.newsfeed.follow.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.follow.entity.Follow;
import com.hanghae.newsfeed.follow.repository.FollowRepository;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
class FollowControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    PostRepository postRepository;

    private static final String BASE_URL = "/api/follows";
    private UserDetailsImpl userDetails;
    private User target;
    private User following;

    @BeforeEach
    void setUp() {
        String email = "1test@test.com";
        String encodedPassword = new BCryptPasswordEncoder().encode("qwe123!@#");
        String nickname = "1test";
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

        User user = userRepository.save(new User(email, nickname, encodedPassword, UserRoleEnum.USER, true));
        userDetails = UserDetailsImpl.builder()
                .id(user.getId())
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .authorities(authorities)
                .build();

        // 팔로우 할 사용자
        target = userRepository.save(new User("target@test.com", "target", "password", UserRoleEnum.USER, true));
        // 이미 팔로우 한 사용자
        following = userRepository.save(new User("following@test.com", "following", "password", UserRoleEnum.USER, true));
        // 팔로우 & 팔로워 목록 조회 시 사용할 mock 데이터
        followRepository.save(new Follow(user, following));
        followRepository.save(new Follow(target, user));
        followRepository.save(new Follow(following, user));

        // 게시물 목록 조회 시 사용할 mock 데이터
        postRepository.save(new Post(following, "title1", "content1"));
        postRepository.save(new Post(following, "title2", "content2"));
        postRepository.save(new Post(following, "title3", "content3"));
    }

    @Test
    @Transactional
    @DisplayName("팔로우 성공 테스트")
    void followUser_success() throws Exception {
        // then
        mvc.perform(post(BASE_URL + "/" + target.getId())
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.follower_id").value(userDetails.getId()))
                .andExpect(jsonPath("$.following_id").value(target.getId()))
                .andExpect(jsonPath("$.msg").value("팔로우 성공"));
    }

    @Test
    @Transactional
    @DisplayName("팔로우 실패 테스트 - 존재하지 않는 사용자")
    void followUser_fail_user_not_exist() throws Exception {
        // given
        long nonExistentUserId = 999L;

        // then
        mvc.perform(post(BASE_URL + "/" + nonExistentUserId)
                        .with(user(userDetails)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 사용자가 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("팔로우 실패 테스트 - 이미 팔로우한 사용자")
    void followUser_fail_already_following() throws Exception {
        // then
        mvc.perform(post(BASE_URL + "/" + following.getId())
                        .with(user(userDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 팔로우한 유저입니다."));
    }

    @Test
    @Transactional
    @DisplayName("팔로우 실패 테스트 - 본인을 팔로우")
    void followUser_fail_follow_self() throws Exception {
        // then
        mvc.perform(post(BASE_URL + "/" + userDetails.getId())
                        .with(user(userDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("자신을 팔로우 할 수 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("팔로우 취소 성공 테스트")
    void unfollowUser_success() throws Exception {
        // then
        mvc.perform(delete(BASE_URL + "/" + following.getId())
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.follower_id").value(userDetails.getId()))
                .andExpect(jsonPath("$.following_id").value(following.getId()))
                .andExpect(jsonPath("$.msg").value("팔로우 취소 성공"));
    }

    @Test
    @Transactional
    @DisplayName("팔로우 취소 실패 테스트 - 존재하지 않는 사용자")
    void unfollowUser_fail_follow_self() throws Exception {
        // given
        long nonExistentUserId = 999L;

        // then
        mvc.perform(delete(BASE_URL + "/" + nonExistentUserId)
                        .with(user(userDetails)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 사용자가 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("팔로우 취소 실패 테스트 - 팔로우 하지 않은 사용자")
    void unfollowUser_fail_not_following() throws Exception {
        // then
        mvc.perform(delete(BASE_URL + "/" + target.getId())
                        .with(user(userDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("해당 유저를 팔로우 하지 않았습니다."));
    }

    @Test
    @Transactional
    @DisplayName("팔로잉 목록 조회 성공 테스트")
    void getFollowingList_success() throws Exception {
        // then
        MvcResult result = mvc.perform(get(BASE_URL + "/followings")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseJson);

        assertEquals(userDetails.getId(), jsonNode.get("content").get(0).get("follower_id").asLong());
        assertEquals(following.getId(), jsonNode.get("content").get(0).get("following_id").asLong());
        assertEquals(1, jsonNode.get("totalElements").asInt());
    }

    @Test
    @Transactional
    @DisplayName("팔로워 목록 조회 성공 테스트")
    void getFollowerList_success() throws Exception {
        // then
        MvcResult result = mvc.perform(get(BASE_URL + "/followers")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseJson);

        assertEquals(target.getId(), jsonNode.get("content").get(0).get("follower_id").asLong());
        assertEquals(userDetails.getId(), jsonNode.get("content").get(0).get("following_id").asLong());
        assertEquals(following.getId(), jsonNode.get("content").get(1).get("follower_id").asLong());
        assertEquals(userDetails.getId(), jsonNode.get("content").get(1).get("following_id").asLong());
        assertEquals(2, jsonNode.get("totalElements").asInt());
    }

    @Test
    @Transactional
    @DisplayName("팔로잉 게시물 목록 조회 성공 테스트")
    void getPostsFromFollowingUsers_success() throws Exception {
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

        assertEquals("title3", jsonNode.get("content").get(0).get("title").asText());
        assertEquals("title2", jsonNode.get("content").get(1).get("title").asText());
        assertEquals("title1", jsonNode.get("content").get(2).get("title").asText());
        assertEquals(3, jsonNode.get("totalElements").asInt());
    }
}