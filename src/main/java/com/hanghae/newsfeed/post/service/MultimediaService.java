package com.hanghae.newsfeed.post.service;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.common.aws.S3UploadService;
import com.hanghae.newsfeed.common.exception.HttpException;
import com.hanghae.newsfeed.post.dto.response.MultimediaResponse;
import com.hanghae.newsfeed.post.entity.Multimedia;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.MultimediaRepository;
import com.hanghae.newsfeed.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MultimediaService {
    private final PostRepository postRepository;
    private final MultimediaRepository multimediaRepository;
    private final S3UploadService s3UploadService;

    // 게시물 멀티미디어 조회
    public List<MultimediaResponse> getMultimediaList(Long postId) {
        List<Multimedia> multimediaList = multimediaRepository.findByPostId(postId);

        return multimediaList.stream()
                .map(multimedia -> new MultimediaResponse(multimedia.getFile_url(), "게시물 멀티미디어 조회 성공"))
                .collect(Collectors.toList());
    }

    // 게시물 멀티미디어 수정
    @Transactional
    public List<MultimediaResponse> updatePostMultimedia(UserDetailsImpl userDetails, Long postId, List<MultipartFile> files) throws IOException {
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

        List<Multimedia> multimediaList = multimediaRepository.findByPostId(postId);

        return multimediaList.stream()
                .map(multimedia -> new MultimediaResponse(multimedia.getFile_url(), "게시물 멀티미디어 수정 성공"))
                .collect(Collectors.toList());
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

    // 게시물 멀티미디어 삭제
    @Transactional
    public List<MultimediaResponse> deleteMultimedia(UserDetailsImpl userDetails, Long postId) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new HttpException(false, "등록된 게시물이 없습니다.", HttpStatus.NOT_FOUND));

        // 게시물 작성자와 현재 로그인한 사용자의 일치 여부 확인
        if (!target.getUser().getId().equals(userDetails.getId())) {
            throw new HttpException(false, "게시물 삭제 권한이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        List<Multimedia> multimediaList = multimediaRepository.findByPostId(postId);

        multimediaRepository.deleteByPost(target);

        return multimediaList.stream()
                .map(multimedia -> new MultimediaResponse(multimedia.getFile_url(), "게시물 멀티미디어 삭제 성공"))
                .collect(Collectors.toList());
    }
}
