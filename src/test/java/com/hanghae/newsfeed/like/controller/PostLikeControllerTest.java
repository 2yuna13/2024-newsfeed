package com.hanghae.newsfeed.like.controller;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.like.entity.PostLike;
import com.hanghae.newsfeed.like.repository.PostLikeRepository;
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

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
class PostLikeControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    private static final String BASE_URL = "/api/likes/posts";
    private UserDetailsImpl userDetails;
    private Post post;
    private Post target;
    private Post postLike;

    @BeforeEach
    void setUp() {
        String email = "1test@test.com";
        String encodedPassword = new BCryptPasswordEncoder().encode("qwe123!@#");
        String nickname = "1test";
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

        User user = userRepository.save(new User(email, nickname, encodedPassword, UserRoleEnum.USER, true));
        User user1 = userRepository.save(new User("like@test.com", "like", encodedPassword, UserRoleEnum.USER, true));
        userDetails = UserDetailsImpl.builder()
                .id(user.getId())
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .authorities(authorities)
                .build();
        // 로그인 한 사용자가 작성한 게시물
        post = postRepository.save(new Post(user, "test", "test"));
        // 좋아요 누를 게시물
        target = postRepository.save(new Post(user1, "unlike", "unlike"));
        // 좋아요 누른 게시물
        postLike = postRepository.save(new Post(user1, "like", "like"));
        postLikeRepository.save(new PostLike(user, postLike));
    }

    @Test
    @Transactional
    @DisplayName("게시물 좋아요 성공 테스트")
    void likePost_success() throws Exception {
        // then
        mvc.perform(post(BASE_URL + "/" + target.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userDetails.getId()))
                .andExpect(jsonPath("$.post_id").value(target.getId()))
                .andExpect(jsonPath("$.msg").value("게시물 좋아요 성공"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 좋아요 실패 테스트 - 존재하지 않는 게시물")
    void likePost_fail_post_not_exist() throws Exception {
        // given
        long nonExistentPostId = 999L;

        // then
        mvc.perform(post(BASE_URL + "/" + nonExistentPostId)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 게시물이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("게시물 좋아요 실패 테스트 - 이미 좋아요 누른 게시물")
    void likePost_fail_already_like() throws Exception {
        // then
        mvc.perform(post(BASE_URL + "/" + postLike.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 좋아요를 눌렀습니다."));
    }

    @Test
    @Transactional
    @DisplayName("게시물 좋아요 실패 테스트 - 작성자 본인 게시물")
    void likePost_fail_like_self() throws Exception {
        // then
        mvc.perform(post(BASE_URL + "/" + post.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("자신의 콘텐츠에는 좋아요를 누를 수 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("게시물 좋아요 취소 성공 테스트")
    void unLikePost_success() throws Exception {
        // then
        mvc.perform(delete(BASE_URL + "/" + postLike.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userDetails.getId()))
                .andExpect(jsonPath("$.post_id").value(postLike.getId()))
                .andExpect(jsonPath("$.msg").value("게시물 좋아요 취소 성공"));
    }

    @Test
    @Transactional
    @DisplayName("게시물 좋아요 취소 실패 테스트 - 존재하지 않는 게시물")
    void unLikePost_fail_post_not_exist() throws Exception {
        // given
        long nonExistentPostId = 999L;

        // then
        mvc.perform(delete(BASE_URL + "/" + nonExistentPostId)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 게시물이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("게시물 좋아요 취소 실패 테스트 - 좋아요를 누르지 않은 게시물")
    void unLikePost_fail_not_like() throws Exception {
        // then
        mvc.perform(delete(BASE_URL + "/" + target.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("아직 좋아요를 누르지 않았습니다."));
    }
}