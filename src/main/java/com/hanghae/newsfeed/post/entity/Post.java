package com.hanghae.newsfeed.post.entity;

import com.hanghae.newsfeed.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public Post(User user, String title, String content, String image) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.image = image;
    }
}
