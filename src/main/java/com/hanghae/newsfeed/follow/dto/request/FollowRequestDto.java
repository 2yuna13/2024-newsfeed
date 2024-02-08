package com.hanghae.newsfeed.follow.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class FollowRequestDto {
    @JsonProperty("following_id")
    private Long followingId;
}