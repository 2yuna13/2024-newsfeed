package com.hanghae.newsfeed.post.service.impl;

import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.post.service.PostService;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시물 목록 조회
    @Override
    public Page<PostResponse> getAllPosts(String keyword,Pageable pageable) {
        Page<Post> allPosts;
        if (keyword!= null) {
            allPosts = postRepository.searchByTitleAndContent(keyword, pageable);
        } else {
            allPosts = postRepository.findAll(pageable);
        }

        return allPosts
                .map(post -> PostResponse.createPostDto(post, "게시물 조회 성공"));
    }

    // 게시물 조회
    @Override
    public PostResponse getPost(Long postId) {
        // 게시물 조회 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        return PostResponse.createPostDto(post, "게시물 조회 성공");
    }

    // 게시물 작성
    @Override
    @Transactional
    public PostResponse createPost(UserDetailsImpl userDetails, PostRequest request) {
        // 유저 조회 예외 발생
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 게시물 엔티티 생성
        Post post = new Post(user, request.getTitle(), request.getContent());

        Post createdPost = postRepository.save(post);

        return PostResponse.createPostDto(createdPost, "게시물 작성 성공");
    }

    // 게시물 수정
    @Override
    @Transactional
    public PostResponse updatePost(UserDetailsImpl userDetails, Long postId, PostRequest request) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        // 게시물 작성자와 현재 로그인한 사용자의 일치 여부 확인
        if (!target.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(CustomErrorCode.NO_EDIT_PERMISSION);
        }

        // 게시물 수정
        target.updatePost(request);

        // DB로 갱신
        Post updatedPost = postRepository.save(target);

        return PostResponse.createPostDto(updatedPost, "게시물 수정 성공");
    }

    // 게시물 삭제
    @Override
    @Transactional
    public PostResponse deletePost(UserDetailsImpl userDetails, Long postId) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        // 게시물 작성자와 현재 로그인한 사용자의 일치 여부 확인
        if (!target.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(CustomErrorCode.NO_DELETE_PERMISSION);
        }
        
        // 게시물 삭제
        postRepository.delete(target);

        return PostResponse.createPostDto(target, "게시물 삭제 성공");
    }
}