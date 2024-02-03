package com.hanghae.newsfeed.post.service;

import com.hanghae.newsfeed.post.dto.request.PostRequestDto;
import com.hanghae.newsfeed.post.dto.response.PostResponseDto;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponseDto create(PostRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("게시물 작성 실패, 등록된 사용자가 없습니다."));

        // 게시물 엔티티 생성
        Post post = new Post(user, requestDto.getTitle(), requestDto.getContent(), requestDto.getImage());

        Post createdPost = postRepository.save(post);

        return PostResponseDto.createPostDto(createdPost, "게시물 작성 성공");
    }
}