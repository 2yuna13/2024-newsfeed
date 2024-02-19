package com.hanghae.newsfeed.comment.service;

import com.hanghae.newsfeed.comment.dto.request.CommentRequest;
import com.hanghae.newsfeed.comment.dto.response.CommentResponse;
import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.comment.repository.CommentRepository;
import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 목록 조회
    public List<CommentResponse> getAllComments(Long postId) {
        List<Comment> allComments = commentRepository.findByPostId(postId);

        return allComments.stream()
                .map(comment -> CommentResponse.createCommentDto(comment, "댓글 조회 성공"))
                .collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public CommentResponse createComment(Long postId, UserDetailsImpl userDetails, CommentRequest request) {
        // 유저 조회 예외 발생
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // 게시물 조회 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        // 댓글 엔티티 생성
        Comment comment = new Comment(user, post, request.getContent());

        Comment createdComment = commentRepository.save(comment);

        return CommentResponse.createCommentDto(createdComment, "댓글 작성 성공");
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(UserDetailsImpl userDetails, Long commentId, CommentRequest request) {
        // 댓글 조회 예외 발생
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자와 현재 로그인한 사용자의 일치 여부 확인
        if (!target.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(CustomErrorCode.NO_EDIT_PERMISSION);
        }

        // 댓글 수정
        target.updateComment(request);

        // DB로 갱신
        Comment updatedComment = commentRepository.save(target);

        return CommentResponse.createCommentDto(updatedComment, "댓글 수정 성공");
    }

    // 댓글 삭제
    @Transactional
    public CommentResponse deleteComment(UserDetailsImpl userDetails, Long commentId) {
        // 댓글 조회 예외 발생
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자와 현재 로그인한 사용자의 일치 여부 확인
        if (!target.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(CustomErrorCode.NO_DELETE_PERMISSION);
        }

        // 댓글 삭제
        commentRepository.delete(target);

        return CommentResponse.createCommentDto(target, "댓글 삭제 성공");
    }
}
