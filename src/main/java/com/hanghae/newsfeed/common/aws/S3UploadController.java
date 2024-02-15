package com.hanghae.newsfeed.common.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class S3UploadController {
    private final S3UploadService s3UploadService;

    @PostMapping
    public ResponseEntity uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
        String url = s3UploadService.uploadProfile(file, "test");
        return new ResponseEntity<>(url, HttpStatus.OK);
    }
}
