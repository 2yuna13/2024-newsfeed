package com.hanghae.newsfeed.post.repository;

import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    Page<Post> findByUserId(Long userId, Pageable pageable);
    Page<Post> findByUserIn(List<User> followingUsers, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.user")
    Page<Post> findAll(Pageable pageable);
}