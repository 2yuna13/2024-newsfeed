package com.hanghae.newsfeed.admin.service;

import com.hanghae.newsfeed.common.exception.HttpException;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPostService {
    private final PostRepository postRepository;

    // 게시물 수정
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new HttpException(false, "등록된 게시물이 없습니다.", HttpStatus.NOT_FOUND));

        // 게시물 수정
        target.updatePost(request);

        // DB로 갱신
        Post updatedPost = postRepository.save(target);

        return PostResponse.createPostDto(updatedPost, "게시물 수정 성공");
    }

    // 게시물 삭제
    @Transactional
    public PostResponse deletePost(Long postId) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new HttpException(false, "등록된 게시물이 없습니다.", HttpStatus.NOT_FOUND));

        // 게시물 삭제
        postRepository.delete(target);

        return PostResponse.createPostDto(target, "게시물 삭제 성공");
    }
}
