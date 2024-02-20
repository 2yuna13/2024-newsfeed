package com.hanghae.newsfeed.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommentRequest {
    @NotBlank
    private String content;
}
