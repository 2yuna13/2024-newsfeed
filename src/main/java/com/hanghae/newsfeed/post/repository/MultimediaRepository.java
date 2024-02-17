package com.hanghae.newsfeed.post.repository;

import com.hanghae.newsfeed.post.entity.Multimedia;
import com.hanghae.newsfeed.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MultimediaRepository extends JpaRepository<Multimedia, Long> {
    void deleteByPost(Post post);
    List<Multimedia> findByPostId(Long postId);
}