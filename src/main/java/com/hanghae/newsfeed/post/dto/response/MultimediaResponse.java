package com.hanghae.newsfeed.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class MultimediaResponse {
    private String fileUrl;
    private String msg;
}
