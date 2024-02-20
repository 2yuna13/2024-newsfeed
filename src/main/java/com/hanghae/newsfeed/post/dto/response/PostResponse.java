package com.hanghae.newsfeed.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanghae.newsfeed.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private String userNickname;
    private String title;
    private String content;
    @JsonProperty("like_count")
    private int likeCount;
    private  String msg;

    public static PostResponse createPostDto(Post post, String msg) {
        return new PostResponse(
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getPostLikes().size(),
                msg
        );
    }
}