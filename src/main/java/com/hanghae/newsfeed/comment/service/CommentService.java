package com.hanghae.newsfeed.comment.service;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.comment.dto.request.CommentRequest;
import com.hanghae.newsfeed.comment.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    /**
     * 댓글 목록 조회
     * @param postId 게시물 아이디
     * @param pageable 페이징 정보
     * @return 해당 게시물 댓글 목록
     */
    Page<CommentResponse> getAllComments(Long postId, Pageable pageable);

    /**
     * 댓글 작성
     * @param postId 게시물 아이디
     * @param userDetails 사용자 상세 정보
     * @param request 댓글 생성 요청
     * @return 생성된 댓글 정보
     */
    CommentResponse createComment(Long postId, UserDetailsImpl userDetails, CommentRequest request);

    /**
     * 댓글 수정
     * @param userDetails 사용자 상세 정보
     * @param commentId 댓글 아이디
     * @param request 댓글 수정 요청
     * @return 수정된 댓글 정보
     */
    CommentResponse updateComment(UserDetailsImpl userDetails, Long commentId, CommentRequest request);

    /**
     * 댓글 삭제
     * @param userDetails 사용자 상세 정보
     * @param commentId 댓글 아이디
     * @return 삭제된 댓글 정보
     */
    CommentResponse deleteComment(UserDetailsImpl userDetails, Long commentId);
}
