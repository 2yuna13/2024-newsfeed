package com.hanghae.newsfeed.comment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CommentRequest {
    private String content;
}
