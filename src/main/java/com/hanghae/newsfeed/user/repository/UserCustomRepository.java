package com.hanghae.newsfeed.user.repository;

import com.hanghae.newsfeed.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCustomRepository {
    Page<User> searchByNickname(String nickname, Boolean isActive, Pageable pageable);
}
