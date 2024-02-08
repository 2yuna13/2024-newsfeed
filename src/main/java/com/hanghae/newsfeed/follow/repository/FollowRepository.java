package com.hanghae.newsfeed.follow.repository;

import com.hanghae.newsfeed.follow.entity.Follow;
import com.hanghae.newsfeed.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);
    Follow findByFollowerAndFollowing(User follower, User following);
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowing(User following);
}
