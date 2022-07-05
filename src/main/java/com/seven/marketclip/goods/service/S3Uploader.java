package com.seven.marketclip.goods.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.seven.marketclip.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.seven.marketclip.exception.ResponseCode.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final List<String> fileList = Arrays.asList(".jpg", ".png", ".jpeg", ".bmp", ".mp4", ".avi");

    public String uploadFile(MultipartFile multipartFile) {
        String fileName = UUID.randomUUID().toString().concat(getFileExtension(multipartFile.getOriginalFilename()));
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new CustomException(FILE_UPLOAD_ERROR);
        }
        return bucketName + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
    }

    public void deleteFile(String fileName) {
        String specFileName = fileName.replaceFirst(bucketName + ".s3.ap-northeast-2.amazonaws.com/", "");
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, specFileName));
    }

    private String getFileExtension(String fileName) {
        String lowerCase = fileName.toLowerCase();
        String target;
        try {
            target = lowerCase.substring(lowerCase.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new CustomException(WRONG_FILE_TYPE);
        }
        if (fileList.contains(target)) {
            return target;
        } else {
            throw new CustomException(WRONG_FILE_TYPE);
        }

    }
}