package com.hanghae.newsfeed.admin.service.impl;

import com.hanghae.newsfeed.admin.service.AdminPostService;
import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPostServiceImpl implements AdminPostService {
    private final PostRepository postRepository;

    // 게시물 수정
    @Override
    @Transactional
    public PostResponse updatePost(Long postId, PostRequest request) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        // 게시물 수정
        target.updatePost(request);

        // DB로 갱신
        Post updatedPost = postRepository.save(target);

        return PostResponse.createPostDto(updatedPost, "게시물 수정 성공");
    }

    // 게시물 삭제
    @Override
    @Transactional
    public PostResponse deletePost(Long postId) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        // 게시물 삭제
        postRepository.delete(target);

        return PostResponse.createPostDto(target, "게시물 삭제 성공");
    }
}
