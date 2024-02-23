package com.hanghae.newsfeed.like.controller;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.comment.repository.CommentRepository;
import com.hanghae.newsfeed.like.entity.CommentLike;
import com.hanghae.newsfeed.like.repository.CommentLikeRepository;
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
class CommentLikeControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;

    private static final String BASE_URL = "/api/likes/comments";
    private UserDetailsImpl userDetails;
    private Comment comment;
    private Comment target;
    private Comment commentLike;

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

        Post post = postRepository.save(new Post(user, "title", "content"));
        // 로그인 한 사용자가 작성한 댓글
        comment = commentRepository.save(new Comment(user, post, "test"));
        // 좋아요 누를 댓글
        target = commentRepository.save(new Comment(user1, post, "unlike"));
        // 좋아요 누른 게시물
        commentLike = commentRepository.save(new Comment(user1, post, "like"));
        commentLikeRepository.save(new CommentLike(user, commentLike));
    }

    @Test
    @Transactional
    @DisplayName("댓글 좋아요 성공 테스트")
    void likeComment_success() throws Exception {
        // then
        mvc.perform(post(BASE_URL + "/" + target.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userDetails.getId()))
                .andExpect(jsonPath("$.comment_id").value(target.getId()))
                .andExpect(jsonPath("$.msg").value("댓글 좋아요 성공"));
    }

    @Test
    @Transactional
    @DisplayName("댓글 좋아요 실패 테스트 - 존재하지 않는 댓글")
    void likeComment_fail_comment_not_exist() throws Exception {
        // given
        long nonExistentCommentId = 999L;

        // then
        mvc.perform(post(BASE_URL + "/" + nonExistentCommentId)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 댓글이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("댓글 좋아요 실패 테스트 - 이미 좋아요 누른 댓글")
    void likeComment_fail_already_like() throws Exception {
        // then
        mvc.perform(post(BASE_URL + "/" + commentLike.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 좋아요를 눌렀습니다."));
    }

    @Test
    @Transactional
    @DisplayName("댓글 좋아요 실패 테스트 - 작성자 본인 댓글")
    void likeComment_fail_like_self() throws Exception {
        // then
        mvc.perform(post(BASE_URL + "/" + comment.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("자신의 콘텐츠에는 좋아요를 누를 수 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("댓글 좋아요 취소 성공 테스트")
    void unLikeComment_success() throws Exception {
        // then
        mvc.perform(delete(BASE_URL + "/" + commentLike.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userDetails.getId()))
                .andExpect(jsonPath("$.comment_id").value(commentLike.getId()))
                .andExpect(jsonPath("$.msg").value("댓글 좋아요 취소 성공"));
    }

    @Test
    @Transactional
    @DisplayName("댓글 좋아요 취소 실패 테스트 - 존재하지 않는 댓글")
    void unLikeComment_fail_post_not_exist() throws Exception {
        // given
        long nonExistentCommentId = 999L;

        // then
        mvc.perform(delete(BASE_URL + "/" + nonExistentCommentId)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 댓글이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("댓글 좋아요 취소 실패 테스트 - 좋아요를 누르지 않은 댓글")
    void unLikeComment_fail_not_like() throws Exception {
        // then
        mvc.perform(delete(BASE_URL + "/" + target.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("아직 좋아요를 누르지 않았습니다."));
    }
}