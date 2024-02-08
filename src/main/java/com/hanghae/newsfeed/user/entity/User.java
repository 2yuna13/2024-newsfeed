package com.hanghae.newsfeed.user.entity;

import com.hanghae.newsfeed.common.Timestamped;
import com.hanghae.newsfeed.follow.entity.Follow;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.user.dto.request.UserRequestDto;
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
@Table(name = "user")
public class User extends Timestamped {
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
    // 유저가 활성 상태인지 확인
    private Boolean active = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PwHistory> pwHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Follow> followerList = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follow> followingList = new ArrayList<>();

    public User(String email, String nickname, String password, UserRoleEnum role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    public void patchUser(UserRequestDto requestDto) {
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

        if (requestDto.getRole() != null) {
            this.role = requestDto.getRole();
        }

        if (requestDto.getActive() != null) {
            this.active = requestDto.getActive();
        }
    }

    public void patchPassword(String newPassword) {
        PwHistory pwHistory = new PwHistory();
        pwHistory.setUser(this);
        pwHistory.setPassword(newPassword);
        this.pwHistories.add(pwHistory);

        this.password = newPassword;
    }
}
