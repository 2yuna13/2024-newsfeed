package com.hanghae.newsfeed.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanghae.newsfeed.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    private String userNickname;
    private String title;
    private String content;
    private String image;
    private  String msg;

    public static PostResponseDto createPostDto(Post post, String msg) {
        return new PostResponseDto(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getImage(),
                msg
        );
    }
}