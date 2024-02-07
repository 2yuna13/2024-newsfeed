package com.hanghae.newsfeed.like.service;

import com.hanghae.newsfeed.like.dto.response.PostLikeResponseDto;
import com.hanghae.newsfeed.like.entity.PostLike;
import com.hanghae.newsfeed.like.repository.PostLikeRepository;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시물 좋아요
    @Transactional
    public PostLikeResponseDto likePost(UserDetailsImpl userDetails, Long postId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("게시물 좋아요 실패, 등록된 사용자가 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물 좋아요 실패, 등록된 게시글이 없습니다."));

        // 작성자 본인이 아닌지 확인
        if (!user.equals(post.getUser())) {
            // 기존 좋아요 여부 확인
            if (postLikeRepository.existsByUserAndPost(user, post)) {
                throw new IllegalArgumentException("게시물 좋아요 실패, 이미 좋아요를 누른 게시글입니다.");
            }

            // 좋아요 등록
            PostLike postLike = new PostLike(user, post);
            PostLike savedPostLike = postLikeRepository.save(postLike);

            return new PostLikeResponseDto(savedPostLike.getId(), user.getId(), post.getId(), "게시물 좋아요 성공");
        } else {
            throw new IllegalArgumentException("게시물 좋아요 실패, 자신의 글에는 좋아요를 누를 수 없습니다.");
        }
    }

    // 게시물 좋아요 취소
    @Transactional
    public PostLikeResponseDto unlikePost(UserDetailsImpl userDetails, Long postId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("게시물 좋아요 취소 실패, 등록된 사용자가 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물 좋아요 취소 실패, 등록된 게시글이 없습니다."));

        // 기존 좋아요 여부 확인
        if (postLikeRepository.existsByUserAndPost(user, post)) {
            PostLike postLike = postLikeRepository.findByUserAndPost(user, post);

            postLikeRepository.delete(postLike);

            return new PostLikeResponseDto(postLike.getId(), user.getId(), post.getId(), "게시물 좋아요 취소 성공");
        } else {
            throw new IllegalArgumentException("게시물 좋아요 취소 실패, 해당 게시글에 좋아요를 누르지 않았습니다.");
        }
    }
}