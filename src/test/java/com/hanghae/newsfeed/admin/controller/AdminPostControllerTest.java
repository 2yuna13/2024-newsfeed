package com.hanghae.newsfeed.admin.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
class AdminPostControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    private static final String BASE_URL = "/api/admins/posts";
    private UserDetailsImpl userDetails;
    private Post post;

    @BeforeEach
    void setUp() {
        String email = "admin@test.com";
        String encodedPassword = new BCryptPasswordEncoder().encode("qwe123!@#");
        String nickname = "admin";
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        User admin = userRepository.save(new User(email, nickname, encodedPassword, UserRoleEnum.ADMIN, true));
        User user = userRepository.save(new User("test@test.com", "test", encodedPassword, UserRoleEnum.USER, true));
        userDetails = UserDetailsImpl.builder()
                .id(admin.getId())
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .authorities(authorities)
                .build();
        post = postRepository.save(new Post(user, "test", "test"));
        // 게시물 목록 조회 시 사용할 mock 데이터
        postRepository.save(new Post(user, "title1", "content1"));
        postRepository.save(new Post(user, "title2", "content2"));
        postRepository.save(new Post(user, "title3", "content3"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 목록 조회 성공 테스트")
    void getAllPosts_success() throws Exception {
        // then
        MvcResult result = mvc.perform(get(BASE_URL)
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
        assertEquals("test", jsonNode.get("content").get(3).get("title").asText());
        assertEquals(4, jsonNode.get("totalElements").asInt());
    }

    @Test
    @Transactional
    @DisplayName("게시물 목록 조회 성공 테스트 - 제목 검색")
    void getAllPosts_success_search() throws Exception {
        // given
        String keyword = "title";

        // then
        MvcResult result = mvc.perform(get(BASE_URL)
                        .param("keyword", keyword)
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

    @Test
    @Transactional
    @DisplayName("게시물 조회 성공 테스트")
    void getPost_success() throws Exception {
        // then
        mvc.perform(get(BASE_URL + "/" + post.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNickname").value(post.getUser().getNickname()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.msg").value("게시물 조회 성공"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 조회 실패 테스트 - 존재하지 않는 게시물")
    void getPost_fail_post_not_exist() throws Exception {
        // given
        long nonExistentPostId = 999L;

        // then
        mvc.perform(get(BASE_URL + "/" + nonExistentPostId)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 게시물이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("게시물 수정 성공 테스트")
    void updatePost_success() throws Exception {
        // given
        String title = "update";
        String content = "update";

        // when
        PostRequest postRequest = new PostRequest(title, content);

        // then
        mvc.perform(patch(BASE_URL + "/" + post.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNickname").value(post.getUser().getNickname()))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.msg").value("게시물 수정 성공"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 수정 실패 테스트 - 존재하지 않는 게시물")
    void updatePost_fail_post_not_exist() throws Exception {
        // given
        long nonExistentPostId = 999L;
        String title = "update";
        String content = "update";

        // when
        PostRequest postRequest = new PostRequest(title, content);

        // then
        mvc.perform(patch(BASE_URL + "/" + nonExistentPostId)
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 게시물이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("게시물 삭제 성공 테스트")
    void deletePost_success() throws Exception {
        // then
        mvc.perform(delete(BASE_URL + "/" + post.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNickname").value(post.getUser().getNickname()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.msg").value("게시물 삭제 성공"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 삭제 실패 테스트 - 존재하지 않는 게시물")
    void deletePost_fail_post_not_exist() throws Exception {
        // given
        long nonExistentPostId = 999L;

        // then
        mvc.perform(delete(BASE_URL + "/" + nonExistentPostId)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 게시물이 없습니다."));
    }
}