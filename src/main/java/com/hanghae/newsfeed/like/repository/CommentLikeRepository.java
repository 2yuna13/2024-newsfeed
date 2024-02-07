package com.hanghae.newsfeed.like.repository;

import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.like.entity.CommentLike;
import com.hanghae.newsfeed.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByUserAndComment(User user, Comment comment);
    CommentLike findByUserAndComment(User user, Comment comment);
}
