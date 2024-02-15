package com.hanghae.newsfeed.common.aws;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 프로필 사진 S3 업로드
//    public String uploadProfile(MultipartFile profileImage) throws IOException {
//        String uniqueFilename = UUID.randomUUID().toString() + "-" + profileImage.getOriginalFilename();
//
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(profileImage.getSize());
//        metadata.setContentType(profileImage.getContentType());
//
//        amazonS3Client.putObject(bucket, uniqueFilename, profileImage.getInputStream(), metadata);
//        return amazonS3Client.getUrl(bucket, uniqueFilename).toString();
//    }

    // 프로필 사진 S3 업로드
    public String uploadProfile(MultipartFile uploadFile, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID();
        String uploadImageUrl = putS3(uploadFile, fileName);

        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(MultipartFile uploadFile, String fileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(uploadFile.getSize());
        metadata.setContentType(uploadFile.getContentType());

        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile.getInputStream(), metadata)
        );

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // S3 이미지 삭제
    public void deleteFile(String filename) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, filename));
        System.out.println(String.format("[%s] deletion complete", filename));
    }
}