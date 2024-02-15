package com.hanghae.newsfeed.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

    @GetMapping("/hi")
    public String niceToMeetYou() {
            return "main/greetings";
    }

    @GetMapping("/login-page")
    public String loginForm() {
        return "user/login";
    }

    @GetMapping("/signup-page")
    public String joinForm() {
        return "user/signup";
    }
}
