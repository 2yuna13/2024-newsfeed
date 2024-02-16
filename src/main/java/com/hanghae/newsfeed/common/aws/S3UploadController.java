package com.hanghae.newsfeed.common.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class S3UploadController {
    private final S3UploadService s3UploadService;

    @PostMapping("/profile")
    public ResponseEntity<String> uploadProfile(@RequestPart("file") MultipartFile file) throws IOException {
        String url = s3UploadService.uploadProfile(file, "test");
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @PostMapping("/multimedia")
    public ResponseEntity<List<String>> uploadMultimedia(@RequestPart("files") List<MultipartFile> files) throws IOException {
        List<String> urls = s3UploadService.uploadMultimedia(files, "test_mul");
        return new ResponseEntity<>(urls, HttpStatus.OK);
    }
}
