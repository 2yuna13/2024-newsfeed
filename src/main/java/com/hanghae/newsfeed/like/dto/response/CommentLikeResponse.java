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
public class CommentLikeResponse {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("comment_id")
    private Long commentId;
    private String msg;
}
