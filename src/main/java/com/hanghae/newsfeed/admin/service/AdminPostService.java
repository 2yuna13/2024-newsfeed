package com.hanghae.newsfeed.admin.service;

import com.hanghae.newsfeed.post.dto.request.PostRequestDto;
import com.hanghae.newsfeed.post.dto.response.PostResponseDto;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPostService {
    private final PostRepository postRepository;

    // 게시물 수정
    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물 수정 실패, 등록된 게시물이 없습니다."));

        // 게시물 수정
        target.patch(requestDto);

        // DB로 갱신
        Post updatedPost = postRepository.save(target);

        return PostResponseDto.createPostDto(updatedPost, "게시물 수정 성공");
    }

    // 게시물 삭제
    @Transactional
    public PostResponseDto deletePost(Long postId) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물 수정 실패, 등록된 게시물이 없습니다."));

        // 게시물 삭제
        postRepository.delete(target);

        return PostResponseDto.createPostDto(target, "게시물 삭제 성공");
    }
}
