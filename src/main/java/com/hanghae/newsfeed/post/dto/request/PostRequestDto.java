package com.hanghae.newsfeed.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    private String title;
    private String content;
    private String image;
}