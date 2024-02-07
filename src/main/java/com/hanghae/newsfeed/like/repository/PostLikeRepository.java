package com.hanghae.newsfeed.like.repository;

import com.hanghae.newsfeed.like.entity.PostLike;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserAndPost(User user, Post post);
}