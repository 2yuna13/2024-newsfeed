package com.hanghae.newsfeed.like.service;

import com.hanghae.newsfeed.common.exception.HttpException;
import com.hanghae.newsfeed.like.dto.response.PostLikeResponse;
import com.hanghae.newsfeed.like.entity.PostLike;
import com.hanghae.newsfeed.like.repository.PostLikeRepository;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시물 좋아요
    @Transactional
    public PostLikeResponse likePost(UserDetailsImpl userDetails, Long postId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new HttpException(false, "등록된 게시물이 없습니다.", HttpStatus.NOT_FOUND));

        // 작성자 본인이 아닌지 확인
        if (!user.equals(post.getUser())) {
            // 기존 좋아요 여부 확인
            if (postLikeRepository.existsByUserAndPost(user, post)) {
                throw new HttpException(false, "이미 좋아요를 누른 게시글입니다.", HttpStatus.BAD_REQUEST);
            }

            // 좋아요 등록
            PostLike postLike = new PostLike(user, post);
            postLikeRepository.save(postLike);

            return new PostLikeResponse(user.getId(), post.getId(), "게시물 좋아요 성공");
        } else {
            throw new HttpException(false, "자신의 게시물에는 좋아요를 누를 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 게시물 좋아요 취소
    @Transactional
    public PostLikeResponse unlikePost(UserDetailsImpl userDetails, Long postId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new HttpException(false, "등록된 게시물이 없습니다.", HttpStatus.NOT_FOUND));

        // 기존 좋아요 여부 확인
        if (postLikeRepository.existsByUserAndPost(user, post)) {
            PostLike postLike = postLikeRepository.findByUserAndPost(user, post);

            postLikeRepository.delete(postLike);

            return new PostLikeResponse(user.getId(), post.getId(), "게시물 좋아요 취소 성공");
        } else {
            throw new HttpException(false, "해당 게시물에 좋아요를 누르지 않았습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}