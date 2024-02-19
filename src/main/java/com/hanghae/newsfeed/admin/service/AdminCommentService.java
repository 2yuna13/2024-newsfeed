package com.hanghae.newsfeed.admin.service;

import com.hanghae.newsfeed.comment.dto.request.CommentRequest;
import com.hanghae.newsfeed.comment.dto.response.CommentResponse;

public interface AdminCommentService {
    /**
     * 댓글 수정
     * @param commentId 댓글 아이디
     * @param request 댓글 수정 요청
     * @return 수정된 댓글 정보
     */
    CommentResponse updateComment(Long commentId, CommentRequest request);

    /**
     * 댓글 삭제
     * @param commentId 댓글 아이디
     * @return 삭제된 댓글 정보
     */
    CommentResponse deleteComment(Long commentId);
}
