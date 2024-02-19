package com.hanghae.newsfeed.like.service.impl;

import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.like.dto.response.PostLikeResponse;
import com.hanghae.newsfeed.like.entity.PostLike;
import com.hanghae.newsfeed.like.repository.PostLikeRepository;
import com.hanghae.newsfeed.like.service.PostLikeService;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시물 좋아요
    @Override
    @Transactional
    public PostLikeResponse likePost(UserDetailsImpl userDetails, Long postId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        // 작성자 본인이 아닌지 확인
        if (!user.equals(post.getUser())) {
            // 기존 좋아요 여부 확인
            if (postLikeRepository.existsByUserAndPost(user, post)) {
                throw new CustomException(CustomErrorCode.ALREADY_LIKED);
            }

            // 좋아요 등록
            PostLike postLike = new PostLike(user, post);
            postLikeRepository.save(postLike);

            return new PostLikeResponse(user.getId(), post.getId(), "게시물 좋아요 성공");
        } else {
            throw new CustomException(CustomErrorCode.CANNOT_LIKE_OWN_CONTENT);
        }
    }

    // 게시물 좋아요 취소
    @Override
    @Transactional
    public PostLikeResponse unlikePost(UserDetailsImpl userDetails, Long postId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        // 기존 좋아요 여부 확인
        if (postLikeRepository.existsByUserAndPost(user, post)) {
            PostLike postLike = postLikeRepository.findByUserAndPost(user, post);

            postLikeRepository.delete(postLike);

            return new PostLikeResponse(user.getId(), post.getId(), "게시물 좋아요 취소 성공");
        } else {
            throw new CustomException(CustomErrorCode.NO_LIKE_YET);
        }
    }
}