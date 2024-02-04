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
        // 유저 조회 예외 발생
        User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("게시물 작성 실패, 등록된 사용자가 없습니다."));

        // 게시물 엔티티 생성
        Post post = new Post(user, requestDto.getTitle(), requestDto.getContent(), requestDto.getImage());

        Post createdPost = postRepository.save(post);

        return PostResponseDto.createPostDto(createdPost, "게시물 작성 성공");
    }

    @Transactional
    public PostResponseDto update(Long id, PostRequestDto requestDto) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물 수정 실패, 등록된 게시물이 없습니다."));

        // 게시물 수정
        target.patch(requestDto);

        // DB로 갱신
        Post updatedPost = postRepository.save(target);

        return PostResponseDto.createPostDto(updatedPost, "게시물 수정 성공");
    }

    @Transactional
    public PostResponseDto delete(Long id) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물 삭제 실패, 등록된 게시물이 없습니다."));
        
        // 게시물 삭제
        postRepository.delete(target);

        return PostResponseDto.createPostDto(target, "게시물 삭제 성공");
    }
}