package com.hanghae.newsfeed.post.entity;

import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.post.dto.request.PostRequestDto;
import com.hanghae.newsfeed.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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

    private String image;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public Post(User user, String title, String content, String image) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public void patch(PostRequestDto requestDto) {
        if (requestDto.getTitle() != null) {
            this.title = requestDto.getTitle();
        }

        if (requestDto.getContent() != null) {
            this.content = requestDto.getContent();
        }

        if (requestDto.getImage() != null) {
            this.image = requestDto.getImage();
        }
    }
}
