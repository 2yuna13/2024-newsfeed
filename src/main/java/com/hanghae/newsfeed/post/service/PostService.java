package com.hanghae.newsfeed.post.service;

import com.hanghae.newsfeed.common.aws.S3UploadService;
import com.hanghae.newsfeed.common.exception.HttpException;
import com.hanghae.newsfeed.post.dto.request.PostRequest;
import com.hanghae.newsfeed.post.dto.response.PostResponse;
import com.hanghae.newsfeed.post.entity.Multimedia;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.MultimediaRepository;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.user.entity.User;
import com.hanghae.newsfeed.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MultimediaRepository multimediaRepository;
    private final S3UploadService s3UploadService;

    // 게시물 목록 조회
    public List<PostResponse> getAllPosts() {
        List<Post> allPosts = postRepository.findAll();

        return allPosts.stream()
                .map(post -> PostResponse.createPostDto(post, "게시물 조회 성공"))
                .collect(Collectors.toList());
    }

    // 게시물 조회
    public PostResponse getPost(Long postId) {
        // 게시물 조회 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new HttpException(false, "등록된 게시물이 없습니다.", HttpStatus.NOT_FOUND));

        return PostResponse.createPostDto(post, "게시물 조회 성공");
    }

    // 게시물 작성
    @Transactional
    public PostResponse createPost(UserDetailsImpl userDetails, PostRequest request, List<MultipartFile> files) throws IOException {
        // 유저 조회 예외 발생
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HttpException(false, "등록된 사용자가 없습니다.", HttpStatus.NOT_FOUND));

        // 게시물 엔티티 생성
        Post post = new Post(user, request.getTitle(), request.getContent());

        // 멀티미디어 엔티티 생성
        updateMultimedia(post, files);

        Post createdPost = postRepository.save(post);

        return PostResponse.createPostDto(createdPost, "게시물 작성 성공");
    }

    // 게시물 수정
    @Transactional
    public PostResponse updatePost(UserDetailsImpl userDetails, Long postId, PostRequest request) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new HttpException(false, "등록된 게시물이 없습니다.", HttpStatus.NOT_FOUND));

        // 게시물 작성자와 현재 로그인한 사용자의 일치 여부 확인
        if (!target.getUser().getId().equals(userDetails.getId())) {
            throw new HttpException(false, "게시물 수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 게시물 수정
        target.updatePost(request);

        // DB로 갱신
        Post updatedPost = postRepository.save(target);

        return PostResponse.createPostDto(updatedPost, "게시물 수정 성공");
    }

    // 게시물 멀티미디어 수정
    @Transactional
    public PostResponse updatePostMultimedia(UserDetailsImpl userDetails, Long postId, List<MultipartFile> files) throws IOException {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new HttpException(false, "등록된 게시물이 없습니다.", HttpStatus.NOT_FOUND));

        // 게시물 작성자와 현재 로그인한 사용자의 일치 여부 확인
        if (!target.getUser().getId().equals(userDetails.getId())) {
            throw new HttpException(false, "게시물 수정 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 해당 게시물 멀티미디어 삭제
        multimediaRepository.deleteByPost(target);

        // 멀티미디어 엔티티 수정(생성)
        updateMultimedia(target, files);

        return PostResponse.createPostDto(target, "게시물 멀티미디어 수정 성공");
    }

    // 멀티미디어 엔티티 생성
    public void updateMultimedia(Post post, List<MultipartFile> files) throws IOException {
        List<Multimedia> newMultimediaList = new ArrayList<>();
        if (!files.isEmpty()) {
            List<String> multimediaUrls = s3UploadService.uploadMultimedia(files, "multimedia");
            newMultimediaList = multimediaUrls.stream().map(url -> new Multimedia(post, url)).toList();
        }

        post.getMultimediaList().addAll(newMultimediaList);
    }

    // 게시물 삭제
    @Transactional
    public PostResponse deletePost(UserDetailsImpl userDetails, Long postId) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new HttpException(false, "등록된 게시물이 없습니다.", HttpStatus.NOT_FOUND));

        // 게시물 작성자와 현재 로그인한 사용자의 일치 여부 확인
        if (!target.getUser().getId().equals(userDetails.getId())) {
            throw new HttpException(false, "게시물 삭제 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }
        
        // 게시물 삭제
        postRepository.delete(target);

        return PostResponse.createPostDto(target, "게시물 삭제 성공");
    }
}