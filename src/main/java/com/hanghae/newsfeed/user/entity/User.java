package com.hanghae.newsfeed.user.entity;

import com.hanghae.newsfeed.admin.dto.request.AdminUserRequest;
import com.hanghae.newsfeed.common.Timestamped;
import com.hanghae.newsfeed.follow.entity.Follow;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.user.dto.request.UserUpdateRequest;
import com.hanghae.newsfeed.user.type.UserRoleEnum;
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

    public User(String email, String nickname, String password, UserRoleEnum role, boolean active) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public void updateUser(UserUpdateRequest request) {
        // 객체 갱신
        if (request.getNickname() != null) {
            this.nickname = request.getNickname();
        }

        if (request.getDescription() != null) {
            this.description = request.getDescription();
        }
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updatePassword(String newPassword) {
        PwHistory pwHistory = new PwHistory();
        pwHistory.setUser(this);
        pwHistory.setPassword(newPassword);
        this.pwHistories.add(pwHistory);

        this.password = newPassword;
    }

    public void updateUserRoleAndActive(AdminUserRequest request) {
        if (request.getRole() != null) {
            this.role = request.getRole();
        }

        if (request.getActive() != null) {
            this.active = request.getActive();
        }
    }
}
