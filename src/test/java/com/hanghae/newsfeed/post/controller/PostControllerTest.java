package com.hanghae.newsfeed.post.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    private static final String BASE_URL = "/api/posts";
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        Long id = 1L;
        String email = "1test@test.com";
        String encodedPassword = new BCryptPasswordEncoder().encode("qwe123!@#");
        String nickname = "1test";
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

        userDetails = UserDetailsImpl.builder()
                .id(id)
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .authorities(authorities)
                .build();
    }

    @Test
    @Transactional
    @DisplayName("게시물 목록 조회 성공 테스트")
    void getAllPosts_success() throws Exception {
        // given
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("테스트를 위한 사용자를 찾을 수 없습니다."));
        postRepository.save(new Post(user, "title1", "content1"));
        postRepository.save(new Post(user, "title2", "content2"));
        postRepository.save(new Post(user, "title3", "content3"));

        // then
        MvcResult result = mvc.perform(get(BASE_URL)
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
    @DisplayName("게시물 목록 조회 성공 테스트 - 제목 검색")
    void getAllPosts_success_search() throws Exception {
        // given
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("테스트를 위한 사용자를 찾을 수 없습니다."));
        postRepository.save(new Post(user, "title1", "content1"));
        postRepository.save(new Post(user, "title2", "content2"));
        postRepository.save(new Post(user, "title3", "content3"));
        postRepository.save(new Post(user, "test", "content4"));

        String keyword = "title";

        // then
        MvcResult result = mvc.perform(get(BASE_URL)
                        .param("keyword", keyword)
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
    @DisplayName("게시물 조회 성공 테스트")
    void getPost_success() throws Exception {
        // given
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("테스트를 위한 사용자를 찾을 수 없습니다."));
        Post savedPost = postRepository.save(new Post(user, "title1", "content1"));

        // then
        mvc.perform(get(BASE_URL + "/" + savedPost.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNickname").value(user.getNickname()))
                .andExpect(jsonPath("$.title").value("title1"))
                .andExpect(jsonPath("$.content").value("content1"))
                .andExpect(jsonPath("$.msg").value("게시물 조회 성공"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 조회 실패 테스트 - 존재하지 않는 게시물")
    void getPost_fail_post_not_exist() throws Exception {
        // given
        Long nonExistentPostId = 999L;

        // then
        mvc.perform(get(BASE_URL + "/" + nonExistentPostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 게시물이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("게시물 작성 성공 테스트")
    void createPost_success() throws Exception {
        // given
        String title = "title";
        String content = "content";

        // when
        PostRequest postRequest = new PostRequest(title, content);

        // then
        mvc.perform(post(BASE_URL).with(user(userDetails))
                .content(mapper.writeValueAsString(postRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userNickname").value(userDetails.getUsername()))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.msg").value("게시물 작성 성공"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 작성 실패 테스트 - 제목 형식(null)")
    void createPost_fail_title_valid() throws Exception {
        // given
        String content = "content";

        // when
        PostRequest postRequest = new PostRequest(null, content);

        // then
        mvc.perform(post(BASE_URL).with(user(userDetails))
                .content(mapper.writeValueAsString(postRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("must not be blank"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 작성 실패 테스트 - 내용 형식(빈칸)")
    void createPost_fail_content_valid() throws Exception {
        // given
        String title = "title";
        String content = " ";

        // when
        PostRequest postRequest = new PostRequest(title, content);

        // then
        mvc.perform(post(BASE_URL).with(user(userDetails))
                .content(mapper.writeValueAsString(postRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("must not be blank"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 수정 성공 테스트")
    void updatePost_success() throws Exception {
        // given
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("테스트를 위한 사용자를 찾을 수 없습니다."));
        Post savedPost = postRepository.save(new Post(user, "title1", "content1"));

        String title = "test";
        String content = "test";

        // when
        PostRequest postRequest = new PostRequest(title, content);

        // then
        mvc.perform(patch(BASE_URL + "/" + savedPost.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNickname").value(userDetails.getUsername()))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.msg").value("게시물 수정 성공"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 수정 실패 테스트 - 존재하지 않는 게시물")
    void updatePost_fail_post_not_exist() throws Exception {
        // given
        Long nonExistentPostId = 999L;
        String title = "test";
        String content = "test";

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
    @DisplayName("게시물 수정 실패 테스트 - 작성자와 사용자 불일치")
    void updatePost_fail_user_different() throws Exception {
        // given
        User user = new User("user1@test.com", "user1", "password", UserRoleEnum.USER, true);
        userRepository.save(user);
        Post savedPost = postRepository.save(new Post(user, "title1", "content1"));

        String title = "test";
        String content = "test";

        // when
        PostRequest postRequest = new PostRequest(title, content);

        // then
        mvc.perform(patch(BASE_URL + "/" + savedPost.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("수정 권한이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("게시물 삭제 성공 테스트")
    void deletePost_success() throws Exception {
        // given
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("테스트를 위한 사용자를 찾을 수 없습니다."));
        Post savedPost = postRepository.save(new Post(user, "title1", "content1"));

        // then
        mvc.perform(delete(BASE_URL + "/" + savedPost.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNickname").value(userDetails.getUsername()))
                .andExpect(jsonPath("$.title").value(savedPost.getTitle()))
                .andExpect(jsonPath("$.content").value(savedPost.getContent()))
                .andExpect(jsonPath("$.msg").value("게시물 삭제 성공"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 삭제 실패 테스트 - 존재하지 않는 게시물")
    void deletePost_fail_post_not_exist() throws Exception {
        // given
        Long nonExistentPostId = 999L;

        // then
        mvc.perform(delete(BASE_URL + "/" + nonExistentPostId)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 게시물이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("게시물 삭제 실패 테스트 - 작성자와 사용자 불일치")
    void deletePost_fail_user_different() throws Exception {
        // given
        User user = new User("user1@test.com", "user1", "password", UserRoleEnum.USER, true);
        userRepository.save(user);
        Post savedPost = postRepository.save(new Post(user, "title1", "content1"));

        // then
        mvc.perform(delete(BASE_URL + "/" + savedPost.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("삭제 권한이 없습니다."));
    }
}