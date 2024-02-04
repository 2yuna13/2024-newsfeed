package com.hanghae.newsfeed.comment.entity;

import com.hanghae.newsfeed.comment.dto.request.CommentRequestDto;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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

    public static Comment creatComment(CommentRequestDto requestDto, User user, Post post) {
        // 예외 처리
        if (requestDto.getId() != null) {
            throw new IllegalArgumentException("댓글 생성 실패, 댓글의 id가 없어야 합니다.");
        }

        // 엔티티 생성 및 반환
        return new Comment(
                requestDto.getId(),
                user,
                post,
                requestDto.getContent()
        );
    }

    public void patch(CommentRequestDto requestDto) {
        if (requestDto.getContent() != null) {
            this.content = requestDto.getContent();
        }
    }
}