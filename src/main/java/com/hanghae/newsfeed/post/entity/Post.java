package com.hanghae.newsfeed.post.entity;

import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.like.entity.PostLike;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
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
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 해당 게시글 엔티티 여러개가 하나의 User에 연관된다
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Multimedia> multimediaList = new ArrayList<>();

    public Post(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public void updatePost(PostRequest request) {
        if (request.getTitle() != null) {
            this.title = request.getTitle();
        }

        if (request.getContent() != null) {
            this.content = request.getContent();
        }
    }
}
