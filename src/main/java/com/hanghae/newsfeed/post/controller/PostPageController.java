package com.hanghae.newsfeed.post.controller;

import com.hanghae.newsfeed.comment.dto.response.CommentResponse;
import com.hanghae.newsfeed.comment.service.impl.CommentServiceImpl;
import com.hanghae.newsfeed.post.dto.response.MultimediaResponse;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.service.impl.MultimediaServiceImpl;
import com.hanghae.newsfeed.post.service.impl.PostServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class PostPageController {
    private final PostServiceImpl postService;
    private final CommentServiceImpl commentService;
    private final MultimediaServiceImpl multimediaService;

    @GetMapping("/posts")
    public String index(
            Model model,
            @RequestParam(required = false) String keyword,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PostResponse> postList = postService.getAllPosts(keyword, pageable);
        model.addAttribute("postList", postList);

        return "post/index";
    }

    @GetMapping("/posts/{postId}")
    public String show(
            Model model,
            @PathVariable Long postId,
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PostResponse post = postService.getPost(postId);
        Page<CommentResponse> commentList = commentService.getAllComments(postId, pageable);
        List<MultimediaResponse> multimediaList = multimediaService.getMultimediaList(postId);
        model.addAttribute("post", post);
        model.addAttribute("commentList", commentList);
        model.addAttribute("multimediaList", multimediaList);

        return "post/show";
    }
}
