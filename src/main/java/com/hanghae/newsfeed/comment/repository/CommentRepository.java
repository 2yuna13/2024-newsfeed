package com.hanghae.newsfeed.comment.repository;

import com.hanghae.newsfeed.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
