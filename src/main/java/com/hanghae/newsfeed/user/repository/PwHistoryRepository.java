package com.hanghae.newsfeed.user.repository;

import com.hanghae.newsfeed.user.entity.PwHistory;
import com.hanghae.newsfeed.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PwHistoryRepository extends JpaRepository<PwHistory, Long> {
    List<PwHistory> findTop3ByUserOrderByCreatedAtDesc(User user);
}