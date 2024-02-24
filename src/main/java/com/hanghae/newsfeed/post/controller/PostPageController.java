package com.hanghae.newsfeed.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostPageController {

    @GetMapping("/posts")
    public String index() {
        return "post/index";
    }
}
