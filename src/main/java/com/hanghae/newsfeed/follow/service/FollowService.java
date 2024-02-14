package com.hanghae.newsfeed.follow.service;

import com.hanghae.newsfeed.follow.dto.response.FollowResponseDto;
import com.hanghae.newsfeed.follow.entity.Follow;
import com.hanghae.newsfeed.follow.repository.FollowRepository;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

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

    // 팔로우 취소
    public FollowResponseDto unfollowUser(Long followingId, UserDetailsImpl userDetails) {
        // 사용자 확인
        User follower = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("팔로우 실패, 등록된 사용자가 없습니다."));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 실패, 등록된 사용자가 없습니다."));

        // 기존 팔로우 여부 확인
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            Follow follow = followRepository.findByFollowerAndFollowing(follower, following);

            followRepository.delete(follow);

            return new FollowResponseDto(follow.getId(), follower.getId(), following.getId(), "팔로우 취소 성공");
        } else {
            throw new IllegalArgumentException("팔로우 취소 실패, 해당 유저를 팔로우 하지 않았습니다.");
        } 
    }
    
    // 팔로잉 목록 조회
    public List<FollowResponseDto> followingList(UserDetailsImpl userDetails) {
        // 사용자 확인
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("팔로잉 목록 조회 실패, 등록된 사용자가 없습니다."));

        List<Follow> followingList = followRepository.findByFollower(currentUser);

        return followingList.stream()
                .map(follow -> new FollowResponseDto(follow.getId(), follow.getFollower().getId(), follow.getFollowing().getId(), "팔로잉 목록 조회 성공"))
                .collect(Collectors.toList());
    }
    
    
    // 팔로워 목록 조회
    public List<FollowResponseDto> followerList(UserDetailsImpl userDetails) {
        // 사용자 확인
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("팔로워 목록 조회 실패, 등록된 사용자가 없습니다."));

        List<Follow> followerList = followRepository.findByFollowing(currentUser);

        return followerList.stream()
                .map(follow -> new FollowResponseDto(follow.getId(), follow.getFollower().getId(), follow.getFollowing().getId(), "팔로워 목록 조회 성공"))
                .collect(Collectors.toList());
    }

    // 내가 팔로우한 유저들이 작성한 게시물 조회
    public List<PostResponse> getPostsFromFollowingUsers(UserDetailsImpl userDetails) {
        // 사용자 확인
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("팔로잉 게시물 조회 실패, 등록된 사용자가 없습니다."));

        // 팔로우 목록
        List<Follow> followingList = followRepository.findByFollower(currentUser);

        // 팔로우 목록에서 각 유저들 추출
        List<User> followingUsers = followingList.stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());

        // 각 유저들의 게시물 조회
        List<Post> postsFromFollowingUsers = postRepository.findByUserIn(followingUsers);

        return postsFromFollowingUsers.stream()
                .map(post -> PostResponse.createPostDto(post, "팔로잉 게시물 조회 성공"))
                .collect(Collectors.toList());
    }
}