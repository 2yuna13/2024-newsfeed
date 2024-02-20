package com.hanghae.newsfeed.comment.entity;

import com.hanghae.newsfeed.comment.dto.request.CommentRequest;
import com.hanghae.newsfeed.like.entity.CommentLike;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 해당 댓글 엔티티 여러개가 하나의 User에 연관된다
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne // 해당 댓글 엔티티 여러개가 하나의 Comment에 연관된다
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentLike> commentLikes = new ArrayList<>();

    public Comment(User user, Post post, String content) {
        this.user = user;
        this.post = post;
        this.content = content;
    }

    public void updateComment(CommentRequest request) {
        if (request.getContent() != null) {
            this.content = request.getContent();
        }
    }
}