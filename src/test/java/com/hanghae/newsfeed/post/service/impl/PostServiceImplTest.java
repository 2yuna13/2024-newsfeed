package com.hanghae.newsfeed.post.service.impl;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import com.hanghae.newsfeed.user.type.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;
    
    @InjectMocks
    PostServiceImpl postService;

    @Test
    @DisplayName("게시물 목록 조회 성공")
    void getAllPosts_success() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        List<Post> mockPosts = Arrays.asList(
                new Post(new User(), "title1", "content1"),
                new Post(new User(), "title2", "content2")
        );
        Page<Post> mockPage = new PageImpl<>(mockPosts);
        when(postRepository.findAll(eq(pageable))).thenReturn(mockPage);

        // when
        Page<PostResponse> result = postService.getAllPosts(null, pageable);

        // then
        verify(postRepository, times(1)).findAll(eq(pageable));
        verify(postRepository, times(0)).searchByTitleAndContent(any(), any());

        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("title1");
        assertThat(result.getContent().get(0).getContent()).isEqualTo("content1");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("title2");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("content2");
    }

    @Test
    @DisplayName("게시물 조회 성공")
    void getPost_success() {
        // given
        Long postId = 1L;
        Post mockPost = new Post(new User(), "title", "content");
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        // when
        PostResponse result = postService.getPost(postId);

        // then
        verify(postRepository, times(1)).findById(postId);

        assertThat(result.getTitle()).isEqualTo(mockPost.getTitle());
        assertThat(result.getContent()).isEqualTo(mockPost.getContent());
    }

    @Test
    @DisplayName("게시물 조회 실패 - 존재하지 않는 게시물")
    void getPost_fail_post_not_exist() {
        // given
        Long nonExistentPostId = 999L;
        when(postRepository.findById(nonExistentPostId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(CustomException.class, () -> postService.getPost(nonExistentPostId),
                "등록된 게시물이 없을 때 예외가 발생해야 합니다.");

        verify(postRepository, times(1)).findById(nonExistentPostId);
    }

    @Test
    @DisplayName("게시물 작성 성공")
    void createPost_success() {
        // given
        // 사용자와 PostRequest 객체 생성
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@email.com", "qwe123!@#", "nickname", authorities);
        PostRequest postRequest = new PostRequest("title", "content");

        // findById 메서드가 호출될 때, User 객체 반환
        when(userRepository.findById(userDetails.getId())).thenReturn(Optional.of(new User()));
        // save 메서드가 호출될 때, 전달된 Post 객체를 그대로 반환
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PostResponse result = postService.createPost(userDetails, postRequest);

        // then
        // 메서드가 1번 올바르게 호출되었는지 확인
        verify(userRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));

        // 생성된 객체의 유효성 검증
        assertThat(result.getTitle()).isEqualTo(postRequest.getTitle());
        assertThat(result.getContent()).isEqualTo(postRequest.getContent());
    }

    @Test
    @DisplayName("게시물 작성 실패 - 존재하지 않는 사용자")
    void createPost_fail_user_not_exist() {
        // given
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(999L, "", "", "", authorities);
        PostRequest postRequest = new PostRequest("title", "content");

        // findById 메서드가 호출될 때, empty() 반환하도록 설정
        when(userRepository.findById(userDetails.getId())).thenReturn(Optional.empty());

        // when, then
        assertThrows(CustomException.class, () -> postService.createPost(userDetails, postRequest),
                "등록된 사용자가 없을 때 예외가 발생해야 합니다.");

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("게시물 작성 실패 - 제목, 내용 형식")
    void createPost_fail_title_content_valid() {
        // given
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@email.com", "qwe123!@#", "nickname", authorities);
        PostRequest postRequestWithNullTitle = new PostRequest(null,"content");
        PostRequest postRequestWithEmptyContent = new PostRequest("title", "");

        // when & then
        assertThrows(CustomException.class, () -> postService.createPost(userDetails, postRequestWithNullTitle),
                "게시물 작성 시 제목이 null이면 예외가 발생해야 합니다.");
        assertThrows(CustomException.class, () -> postService.createPost(userDetails, postRequestWithEmptyContent),
                "게시물 작성 시 내용이 비어 있으면 예외가 발생해야 합니다.");
    }

    @Test
    @DisplayName("게시물 수정 성공")
    void updatePost_success() {
        // given
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@email.com", "qwe123!@#", "nickname", authorities);
        User user = new User("test@email.com", "nickname", "qwe123!@#", UserRoleEnum.USER, true);
        ReflectionTestUtils.setField(user, "id", 1L);
        Long postId = 1L;
        PostRequest postRequest = new PostRequest("update", "update");

        Post existingPost = new Post(user, "title", "content");
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        PostResponse result = postService.updatePost(userDetails, postId, postRequest);

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any(Post.class));

        assertThat(result.getTitle()).isEqualTo(postRequest.getTitle());
        assertThat(result.getContent()).isEqualTo(postRequest.getContent());
    }

    @Test
    @DisplayName("게시물 수정 실패 - 존재하지 않는 게시물")
    void updatePost_fail_post_not_exist() {
        // given
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@email.com", "qwe123!@#", "nickname", authorities);
        Long nonExistentPostId = 999L;
        PostRequest postRequest = new PostRequest("update", "update");

        when(postRepository.findById(nonExistentPostId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(CustomException.class, () -> postService.updatePost(userDetails, nonExistentPostId, postRequest),
                "등록된 게시물이 없을 때 예외가 발생해야 합니다.");

        verify(postRepository, times(1)).findById(nonExistentPostId);
    }


    @Test
    @DisplayName("게시물 수정 실패 - 작성자와 사용자 불일치")
    void updatePost_fail_user_different() {
        // given
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@email.com", "qwe123!@#", "nickname", authorities);
        User user = new User("test1@email.com", "nickname", "password", UserRoleEnum.USER, true);
        ReflectionTestUtils.setField(user, "id", 2L);
        Long postId = 1L;
        PostRequest postRequest = new PostRequest("update", "update");

        Post existingPost = new Post(user, "title", "content");
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        // when, then
        assertThrows(CustomException.class, () -> postService.updatePost(userDetails, postId, postRequest),
                "작성자와 사용자가 일치하지 않을 때 예외가 발생해야 합니다.");

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    @DisplayName("게시물 삭제 성공")
    void deletePost_success() {
        // given
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@email.com", "qwe123!@#", "nickname", authorities);
        User user = new User("test@email.com", "nickname", "qwe123!@#", UserRoleEnum.USER, true);
        ReflectionTestUtils.setField(user, "id", 1L);
        Long postId = 1L;

        Post targetPost = new Post(user, "title", "content");
        when(postRepository.findById(postId)).thenReturn(Optional.of(targetPost));

        // when
        PostResponse result = postService.deletePost(userDetails, postId);

        // then
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).delete(targetPost);

        assertThat(result.getTitle()).isEqualTo(targetPost.getTitle());
        assertThat(result.getContent()).isEqualTo(targetPost.getContent());
    }

    @Test
    @DisplayName("게시물 삭제 실패 - 존재하지 않는 게시물")
    void deletePost_success_post_not_exist() {
        // given
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@email.com", "qwe123!@#", "nickname", authorities);
        User user = new User("test@email.com", "nickname", "qwe123!@#", UserRoleEnum.USER, true);
        ReflectionTestUtils.setField(user, "id", 1L);
        Long nonExistentPostId = 999L;

        when(postRepository.findById(nonExistentPostId)).thenReturn(Optional.empty());

        // when, then
        assertThrows(CustomException.class, () -> postService.deletePost(userDetails, nonExistentPostId),
                "등록된 게시물이 없을 때 예외가 발생해야 합니다.");

        verify(postRepository, times(1)).findById(nonExistentPostId);
    }


    @Test
    @DisplayName("게시물 삭제 실패 - 작성자와 사용자 불일치")
    void deletePost_fail_user_different() {
        // given
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@email.com", "qwe123!@#", "nickname", authorities);
        User user = new User("test@email.com", "nickname", "qwe123!@#", UserRoleEnum.USER, true);
        ReflectionTestUtils.setField(user, "id", 2L);
        Long postId = 1L;

        Post targetPost = new Post(user, "title", "content");
        when(postRepository.findById(postId)).thenReturn(Optional.of(targetPost));

        // when, then
        assertThrows(CustomException.class, () -> postService.deletePost(userDetails, postId),
                "작성자와 사용자가 일치하지 않을 때 예외가 발생해야 합니다.");

        verify(postRepository, times(1)).findById(postId);
    }
}