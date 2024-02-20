package com.hanghae.newsfeed.follow.service.impl;

import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.follow.dto.response.FollowResponse;
import com.hanghae.newsfeed.follow.entity.Follow;
import com.hanghae.newsfeed.follow.repository.FollowRepository;
import com.hanghae.newsfeed.follow.service.FollowService;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 팔로우
    @Override
    @Transactional
    public FollowResponse followUser(Long followingId, UserDetailsImpl userDetails) {
        // 사용자 확인
        User follower = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 본인인지 아닌지 확인
        if (!follower.equals(following)) {
            // 기존 팔로우 여부 확인
            if (followRepository.existsByFollowerAndFollowing(follower, following)) {
                throw new CustomException(CustomErrorCode.ALREADY_FOLLOWING);
            }

            // 팔로우 등록
            Follow follow = new Follow(follower, following);
            followRepository.save(follow);

            return new FollowResponse(follower.getId(), following.getId(), "팔로우 성공");
        } else{
            throw new CustomException(CustomErrorCode.CANNOT_FOLLOW_SELF);
        }
    }

    // 팔로우 취소
    @Override
    @Transactional
    public FollowResponse unfollowUser(Long followingId, UserDetailsImpl userDetails) {
        // 사용자 확인
        User follower = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 기존 팔로우 여부 확인
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            Follow follow = followRepository.findByFollowerAndFollowing(follower, following);

            followRepository.delete(follow);

            return new FollowResponse(follower.getId(), following.getId(), "팔로우 취소 성공");
        } else {
            throw new CustomException(CustomErrorCode.NO_FOLLOW_YET);
        } 
    }
    
    // 팔로잉 목록 조회
    @Override
    public Page<FollowResponse> followingList(UserDetailsImpl userDetails, Pageable pageable) {
        // 사용자 확인
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Page<Follow> followingList = followRepository.findByFollower(currentUser, pageable);

        return followingList
                .map(follow -> new FollowResponse(follow.getFollower().getId(), follow.getFollowing().getId(), "팔로잉 목록 조회 성공"));
    }
    
    
    // 팔로워 목록 조회
    @Override
    public Page<FollowResponse> followerList(UserDetailsImpl userDetails, Pageable pageable) {
        // 사용자 확인
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Page<Follow> followerList = followRepository.findByFollowing(currentUser, pageable);

        return followerList
                .map(follow -> new FollowResponse(follow.getFollower().getId(), follow.getFollowing().getId(), "팔로워 목록 조회 성공"));
    }

    // 내가 팔로우한 유저들이 작성한 게시물 조회
    @Override
    public Page<PostResponse> getPostsFromFollowingUsers(UserDetailsImpl userDetails, Pageable pageable) {
        // 사용자 확인
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 팔로우 목록
        List<Follow> followingList = followRepository.findByFollower(currentUser);

        // 팔로우 목록에서 각 유저들 추출
        List<User> followingUsers = followingList.stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());

        // 각 유저들의 게시물 조회
        Page<Post> postsFromFollowingUsers = postRepository.findByUserIn(followingUsers, pageable);

        return postsFromFollowingUsers
                .map(post -> PostResponse.createPostDto(post, "팔로잉 게시물 조회 성공"));
    }
}