package com.hanghae.newsfeed.post.repository;

import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    Page<Post> findByUserIn(List<User> followingUsers, Pageable pageable);
}