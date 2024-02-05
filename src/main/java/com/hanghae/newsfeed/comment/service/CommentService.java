package com.hanghae.newsfeed.comment.service;

import com.hanghae.newsfeed.comment.dto.request.CommentRequestDto;
import com.hanghae.newsfeed.comment.dto.response.CommentResponseDto;
import com.hanghae.newsfeed.comment.entity.Comment;
import com.hanghae.newsfeed.comment.repository.CommentRepository;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    public List<CommentResponseDto> getAllComments(Long postId) {
        List<Comment> allComments = commentRepository.findByPostId(postId);

        return allComments.stream()
                .map(comment -> CommentResponseDto.createCommentDto(comment, "댓글 조회 성공"))
                .collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto requestDto) {
        // 유저 조회 예외 발생
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("댓글 작성 실패, 등록된 사용자가 없습니다."));

        // 게시물 조회 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 작성 실패, 등록된 게시물이 없습니다."));

        // 댓글 엔티티 생성
        Comment comment = Comment.creatComment(requestDto, user, post);

        Comment createdComment = commentRepository.save(comment);

        return CommentResponseDto.createCommentDto(createdComment, "댓글 작성 성공");
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        // 댓글 조회 예외 발생
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(" 댓글 수정 실패, 해당 댓글이 없습니다."));

        // 댓글 작성자와 현재 로그인한 사용자의 일치 여부 확인 && 관리자 확인
        if (!target.getUser().getId().equals(userDetails.getId()) && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new IllegalStateException("댓글 수정 권한이 없습니다.");
        }

        // 댓글 수정
        target.patch(requestDto);

        // DB로 갱신
        Comment updatedComment = commentRepository.save(target);

        return CommentResponseDto.createCommentDto(updatedComment, "댓글 수정 성공");
    }

    // 댓글 삭제
    @Transactional
    public CommentResponseDto deleteComment(Long commentId, UserDetailsImpl userDetails) {
        // 댓글 조회 예외 발생
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(" 댓글 삭제 실패, 해당 댓글이 없습니다."));

        // 댓글 작성자와 현재 로그인한 사용자의 일치 여부 확인 && 관리자 확인
        if (!target.getUser().getId().equals(userDetails.getId()) && !userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new IllegalStateException("댓글 삭제 권한이 없습니다.");
        }

        // 댓글 삭제
        commentRepository.delete(target);

        return CommentResponseDto.createCommentDto(target, "댓글 삭제 성공");
    }
}
