package com.hanghae.newsfeed.like.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeResponse {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("post_id")
    private Long postId;
    private String msg;
}