package com.hanghae.newsfeed.post.repository;

import com.hanghae.newsfeed.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {
    Page<Post> searchByTitleAndContent(String keyword, Pageable pageable);
}
