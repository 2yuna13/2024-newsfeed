package com.hanghae.newsfeed.post.service.impl;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.common.aws.S3UploadService;
import com.hanghae.newsfeed.common.exception.CustomErrorCode;
import com.hanghae.newsfeed.common.exception.CustomException;
import com.hanghae.newsfeed.post.dto.response.MultimediaResponse;
import com.hanghae.newsfeed.post.entity.Multimedia;
import com.hanghae.newsfeed.post.entity.Post;
import com.hanghae.newsfeed.post.repository.MultimediaRepository;
import com.hanghae.newsfeed.post.repository.PostRepository;
import com.hanghae.newsfeed.post.service.MultimediaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MultimediaServiceImpl implements MultimediaService {
    private final PostRepository postRepository;
    private final MultimediaRepository multimediaRepository;
    private final S3UploadService s3UploadService;

    // 게시물 멀티미디어 조회
    @Override
    public List<MultimediaResponse> getMultimediaList(Long postId) {
        // 게시물 조회 예외 발생
        postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        List<Multimedia> multimediaList = multimediaRepository.findByPostId(postId);

        return multimediaList.stream()
                .map(multimedia -> new MultimediaResponse(multimedia.getFile_url(), "게시물 멀티미디어 조회 성공"))
                .collect(Collectors.toList());
    }

    // 게시물 멀티미디어 수정
    @Override
    @Transactional
    public List<MultimediaResponse> updatePostMultimedia(UserDetailsImpl userDetails, Long postId, List<MultipartFile> files) throws IOException {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        // 게시물 작성자와 현재 로그인한 사용자의 일치 여부 확인
        if (!target.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(CustomErrorCode.NO_EDIT_PERMISSION);
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
    @Override
    @Transactional
    public List<MultimediaResponse> deleteMultimedia(UserDetailsImpl userDetails, Long postId) {
        // 게시물 조회 예외 발생
        Post target = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.POST_NOT_FOUND));

        // 게시물 작성자와 현재 로그인한 사용자의 일치 여부 확인
        if (!target.getUser().getId().equals(userDetails.getId())) {
            throw new CustomException(CustomErrorCode.NO_DELETE_PERMISSION);
        }

        List<Multimedia> multimediaList = multimediaRepository.findByPostId(postId);

        multimediaRepository.deleteByPost(target);

        return multimediaList.stream()
                .map(multimedia -> new MultimediaResponse(multimedia.getFile_url(), "게시물 멀티미디어 삭제 성공"))
                .collect(Collectors.toList());
    }
}
