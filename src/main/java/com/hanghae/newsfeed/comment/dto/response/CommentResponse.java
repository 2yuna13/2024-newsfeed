package com.hanghae.newsfeed.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanghae.newsfeed.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    @JsonProperty("post_id")
    private Long postId;
    private String userNickname;
    @JsonProperty("post_id")
    private Long postId;
    private String content;
    private String msg;
    @JsonProperty("like_count")
    private int likeCount;

    public static CommentResponse createCommentDto(Comment comment, String msg) {
        return new CommentResponse(
                comment.getPost().getId(),
                comment.getUser().getNickname(),
                comment.getPost().getId(),
                comment.getContent(),
                msg,
                comment.getCommentLikes().size()
        );
    }
}
