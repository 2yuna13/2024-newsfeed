package com.hanghae.newsfeed.follow.service;

import com.hanghae.newsfeed.follow.dto.response.FollowResponseDto;
import com.hanghae.newsfeed.follow.entity.Follow;
import com.hanghae.newsfeed.follow.repository.FollowRepository;
import com.hanghae.newsfeed.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    // 팔로우
    @Transactional
    public FollowResponseDto followUser(Long followingId, UserDetailsImpl userDetails) {
        // 사용자 확인
        User follower = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("팔로우 실패, 등록된 사용자가 없습니다."));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 실패, 등록된 사용자가 없습니다."));

        // 본인인지 아닌지 확인
        if (!follower.equals(following)) {
            // 기존 팔로우 여부 확인
            if (followRepository.existsByFollowerAndFollowing(follower, following)) {
                throw new IllegalArgumentException("이미 팔로우한 유저입니다.");
            }

            // 팔로우 등록
            Follow follow = new Follow(follower, following);
            Follow savedFollow = followRepository.save(follow);

            return new FollowResponseDto(savedFollow.getId(), follower.getId(), following.getId(), "팔로우 성공");
        } else{
            throw new IllegalArgumentException("팔로우 실패, 자신을 팔로우 할 수 없습니다.");
        }
    }
}