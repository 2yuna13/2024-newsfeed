package com.hanghae.newsfeed.comment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.comment.dto.request.CommentRequest;
import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.comment.repository.CommentRepository;
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
class CommentControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    private static final String BASE_URL = "/api/comments";
    private UserDetailsImpl userDetails;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        String email = "1test@test.com";
        String encodedPassword = new BCryptPasswordEncoder().encode("qwe123!@#");
        String nickname = "1test";
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

        User user = userRepository.save(new User(email, nickname, encodedPassword, UserRoleEnum.USER, true));
        post = postRepository.save(new Post(user, "title", "content"));
        comment = commentRepository.save(new Comment(user, post, "test"));
        userDetails = UserDetailsImpl.builder()
                .id(user.getId())
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .authorities(authorities)
                .build();

        // 댓글 목록 조회 시 사용할 mock 데이터
        commentRepository.save(new Comment(user, post, "content1"));
        commentRepository.save(new Comment(user, post, "content2"));
        commentRepository.save(new Comment(user, post, "content3"));
    }

    @Test
    @Transactional
    @DisplayName("댓글 목록 조회 성공 테스트")
    void getAllComments_success() throws Exception {
        // then
        MvcResult result = mvc.perform(get(BASE_URL + "/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseJson);

        // 예상 결과와 일치하는지 확인
        assertEquals("content3", jsonNode.get("content").get(0).get("content").asText());
        assertEquals("content2", jsonNode.get("content").get(1).get("content").asText());
        assertEquals("content1", jsonNode.get("content").get(2).get("content").asText());
        assertEquals("test", jsonNode.get("content").get(3).get("content").asText());
        assertEquals(4, jsonNode.get("totalElements").asInt());
    }


    @Test
    @Transactional
    @DisplayName("댓글 목록 조회 실패 테스트 - 존재하지 않는 게시물")
    void getAllComments_fail_post_not_exist() throws Exception {
        // given
        long nonExistentPostId = 999L;

        // then
        mvc.perform(get(BASE_URL + "/" + nonExistentPostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 게시물이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("댓글 작성 성공 테스트")
    void createComment_success() throws Exception {
        // given
        String content = "content";

        // when
        CommentRequest commentRequest = new CommentRequest(content);

        // then
        mvc.perform(post(BASE_URL + "/" + post.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userNickname").value(userDetails.getUsername()))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.post_id").value(post.getId()))
                .andExpect(jsonPath("$.msg").value("댓글 작성 성공"));
    }

    @Test
    @Transactional
    @DisplayName("댓글 작성 실패 테스트 - 내용 형식(빈칸)")
    void createComment_fail_content_valid() throws Exception {
        // given
        String content = " ";

        // when
        CommentRequest commentRequest = new CommentRequest(content);

        // then
        mvc.perform(post(BASE_URL + "/" + post.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("must not be blank"));
    }

    @Test
    @Transactional
    @DisplayName("댓글 작성 실패 테스트 - 존재하지 않는 게시물")
    void createComment_fail_post_not_exist() throws Exception {
        // given
        long nonExistentPostId = 999L;
        String content = "content";

        // when
        CommentRequest commentRequest = new CommentRequest(content);

        // then
        mvc.perform(post(BASE_URL + "/" + nonExistentPostId)
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 게시물이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("댓글 수정 성공 테스트")
    void updateComment_success() throws Exception {
        // given
        String content = "update";

        // when
        CommentRequest commentRequest = new CommentRequest(content);

        // then
        mvc.perform(patch(BASE_URL + "/" + comment.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNickname").value(userDetails.getUsername()))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.post_id").value(post.getId()))
                .andExpect(jsonPath("$.msg").value("댓글 수정 성공"));
    }

    @Test
    @Transactional
    @DisplayName("댓글 수정 실패 테스트 - 존재하지 않는 댓글")
    void updateComment_fail_comment_not_exist() throws Exception {
        // given
        long nonExistentCommentId = 999L;
        String content = "update";

        // when
        CommentRequest commentRequest = new CommentRequest(content);

        // then
        mvc.perform(patch(BASE_URL + "/" + nonExistentCommentId)
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("등록된 댓글이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("댓글 수정 실패 테스트 - 작성자와 사용자 불일치")
    void updateComment_fail_user_different() throws Exception {
        // given
        User differentUser = userRepository.save(new User("user1@test.com", "user1", "password", UserRoleEnum.USER, true));
        Post savedPost = postRepository.save(new Post(differentUser, "title1", "content1"));
        Comment savedComment = commentRepository.save(new Comment(differentUser, savedPost, "content1"));

        String content = "update";

        // when
        CommentRequest commentRequest = new CommentRequest(content);

        // then
        mvc.perform(patch(BASE_URL + "/" + savedComment.getId())
                        .with(user(userDetails))
                        .content(mapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("수정 권한이 없습니다."));
    }

    @Test
    @Transactional
    @DisplayName("댓글 삭제 성공 테스트")
    void deleteComment_success() throws Exception {
        // then
        mvc.perform(delete(BASE_URL + "/" + comment.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNickname").value(userDetails.getUsername()))
                .andExpect(jsonPath("$.content").value(comment.getContent()))
                .andExpect(jsonPath("$.post_id").value(post.getId()))
                .andExpect(jsonPath("$.msg").value("댓글 삭제 성공"));
    }

    @Test
    @Transactional
    @DisplayName("댓글 삭제 실패 테스트 - 존재하지 않는 댓글")
    void deleteComment_fail_comment_not_exist() throws Exception {
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
    @DisplayName("댓글 삭제 실패 테스트 - 작성자와 사용자 불일치")
    void deleteComment_fail_user_different() throws Exception {
        // given
        User differentUser = userRepository.save(new User("user1@test.com", "user1", "password", UserRoleEnum.USER, true));
        Post savedPost = postRepository.save(new Post(differentUser, "title1", "content1"));
        Comment savedComment = commentRepository.save(new Comment(differentUser, savedPost, "content1"));

        // then
        mvc.perform(delete(BASE_URL + "/" + savedComment.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("삭제 권한이 없습니다."));
    }
}