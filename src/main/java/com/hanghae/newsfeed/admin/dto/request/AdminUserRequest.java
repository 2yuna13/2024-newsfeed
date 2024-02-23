package com.hanghae.newsfeed.admin.dto.request;

import com.hanghae.newsfeed.user.type.UserRoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRequest {
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;
    private Boolean active;
}
