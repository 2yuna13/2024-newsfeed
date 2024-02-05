package com.hanghae.newsfeed.user.entity;

import com.hanghae.newsfeed.user.dto.request.UserRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private String description;
    private String profileImage;

    public User(String email, String nickname, String password, UserRoleEnum role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    public void patch(UserRequestDto requestDto) {
        // 객체 갱신
        if (requestDto.getNickname() != null) {
            this.nickname = requestDto.getNickname();
        }

        if (requestDto.getDescription() != null) {
            this.description = requestDto.getDescription();
        }

        if (requestDto.getProfileImage() != null) {
            this.profileImage = requestDto.getProfileImage();
        }
    }
}
