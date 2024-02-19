package com.hanghae.newsfeed.post.service;

import com.hanghae.newsfeed.auth.security.UserDetailsImpl;
import com.hanghae.newsfeed.post.dto.response.MultimediaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MultimediaService {
    /**
     * 게시물 멀티미디어 조회
     * @param postId 게시물 아이디
     * @return 게시물 멀티미디어 목록
     */
    List<MultimediaResponse> getMultimediaList(Long postId);

    /**
     * 게시물 멀티미디어 수정
     * @param userDetails 사용자 상세 정보
     * @param postId 게시물 아이디
     * @param files 멀티미디어 파일 목록
     * @return 수정된 게시물 멀티미디어 목록
     */
    List<MultimediaResponse> updatePostMultimedia(UserDetailsImpl userDetails, Long postId, List<MultipartFile> files) throws IOException;

    /**
     * 게시물 멀티미디어 삭제
     * @param userDetails 사용자 상세 정보
     * @param postId 게시물 아이디
     * @return 삭제된 게시물 멀티미디어 목록
     */
    List<MultimediaResponse> deleteMultimedia(UserDetailsImpl userDetails, Long postId) throws IOException;
}
