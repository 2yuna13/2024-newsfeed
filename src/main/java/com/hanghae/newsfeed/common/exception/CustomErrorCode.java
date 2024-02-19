package com.hanghae.newsfeed.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {
    NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // user
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일이 존재합니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임이 존재합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 사용자가 없습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다"),
    DUPLICATED_RECENT_PASSWORD(HttpStatus.BAD_REQUEST, "최근에 사용한 비밀번호와 중복되어 사용할 수 없습니다."),
    USER_DEACTIVATED(HttpStatus.BAD_REQUEST, "해당 계정은 탈퇴되었습니다."),
    ALREADY_LOGOUT(HttpStatus.BAD_REQUEST, "이미 로그아웃한 사용자입니다."),
    ADMIN_CANNOT_BE_MODIFIED(HttpStatus.BAD_REQUEST, "관리자의 권한은 수정할 수 없습니다."),

    // token
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.BAD_REQUEST, "Refresh 토큰이 일치하지 않습니다."),

    // post, comment
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 게시물이 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 댓글이 없습니다."),
    NO_EDIT_PERMISSION(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다."),
    NO_DELETE_PERMISSION(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다."),

    // like
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다."),
    CANNOT_LIKE_OWN_CONTENT(HttpStatus.BAD_REQUEST, "자신의 콘텐츠에는 좋아요를 누를 수 없습니다."),
    NO_LIKE_YET(HttpStatus.BAD_REQUEST, "아직 좋아요를 누르지 않았습니다."),

    // follow
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "이미 팔로우한 유저입니다."),
    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "자신을 팔로우 할 수 없습니다."),
    NO_FOLLOW_YET(HttpStatus.BAD_REQUEST, "해당 유저를 팔로우 하지 않았습니다."),;

    private final HttpStatus httpStatus;
    private final String message;
}
