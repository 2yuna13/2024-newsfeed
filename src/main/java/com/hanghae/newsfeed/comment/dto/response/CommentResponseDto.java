package com.hanghae.newsfeed.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanghae.newsfeed.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("post_id")
    private Long postId;
    private String content;
    private String msg;

    public static CommentResponseDto createCommentDto(Comment comment, String msg) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getPost().getId(),
                comment.getContent(),
                msg
        );
    }
}
