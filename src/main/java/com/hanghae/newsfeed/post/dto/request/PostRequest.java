package com.hanghae.newsfeed.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}