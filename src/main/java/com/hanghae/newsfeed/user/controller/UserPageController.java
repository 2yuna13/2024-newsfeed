package com.hanghae.newsfeed.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class UserPageController {

    @GetMapping("/home")
    public String home() {
        return "main/home";
    }

    @GetMapping("/login-page")
    public String loginForm() {
        return "user/login";
    }

    @GetMapping("/myPage")
    public String myPage() {
        return "user/myPage";
    }
}
