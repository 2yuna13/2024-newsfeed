package com.hanghae.newsfeed.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class UserPageController {

    @GetMapping("/home")
    public String home(Model model) {

        // 현재 사용자의 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 유저 정보를 모델에 추가
        model.addAttribute("userDetails", userDetails);

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
