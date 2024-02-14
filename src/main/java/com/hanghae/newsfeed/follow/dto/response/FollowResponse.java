package com.hanghae.newsfeed.follow.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowResponse {
    @JsonProperty("follower_id")
    private Long followerId;
    @JsonProperty("following_id")
    private Long followingId;
    private String msg;
}