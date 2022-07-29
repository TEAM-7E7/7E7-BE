package com.seven.marketclip.cloud_server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.image.domain.AccountImage;
import com.seven.marketclip.image.domain.GoodsImage;
import com.seven.marketclip.image.repository.AccountImageRepository;
import com.seven.marketclip.image.repository.GoodsImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.seven.marketclip.exception.ResponseCode.*;

@Slf4j
@Component
public class S3CloudServiceImpl implements FileCloudService {

    private final AmazonS3 amazonS3;
    private final GoodsImageRepository goodsImageRepository;
    private final AccountImageRepository accountImageRepository;

    public S3CloudServiceImpl(AmazonS3 amazonS3, GoodsImageRepository goodsImageRepository, AccountImageRepository accountImageRepository) {
        this.amazonS3 = amazonS3;
        this.goodsImageRepository = goodsImageRepository;
        this.accountImageRepository = accountImageRepository;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;
    private final String protocol = "https://";
    private final List<String> fileList =
            Arrays.asList(".jpg", ".png", ".jpeg", "gif", ".svg", ".mp4", ".m4v", ".avi", ".wmv", ".mwa", ".asf", ".mpg", ".mpeg", ".mkv");

    @Override
    public String uploadFile(MultipartFile multipartFile) throws CustomException {
        String fileName = UUID.randomUUID().toString().concat(getFileExtension(multipartFile.getOriginalFilename()));
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));    // 권한 추가 (publicRead)
        } catch (IOException e) {
            throw new CustomException(FILE_UPLOAD_ERROR);
        }
        return protocol + bucket + ".s3." + region + ".amazonaws.com/" + fileName;

    }

    @Override
    public void deleteFile(String fileUrl) {
        String fileKey = fileUrl.split(".s3." + region + ".amazonaws.com/")[1];
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileKey));
    }

    @Override
    @Transactional
    public void scheduledClearance() {
        List<GoodsImage> unusedUrls = goodsImageRepository.findAllByGoodsIdIsNull(LocalDateTime.now().minusMinutes(60));
        for (GoodsImage goodsImage : unusedUrls) {
            deleteFile(goodsImage.getImageUrl());
            goodsImageRepository.deleteById(goodsImage.getId());
        }
    }

    @Override
    public void cascadeGoodsImage(Long accountId){
        List<GoodsImage> cascadeUrls = goodsImageRepository.findAllByUploaderId(accountId);
        for (GoodsImage goodsImage : cascadeUrls) {
            deleteFile(goodsImage.getImageUrl());
//            goodsImageRepository.deleteById(goodsImage.getId());
        }
    }

    @Override
    public void cascadeAccountImage(Long goodsId){
        AccountImage accountImage = accountImageRepository.findByAccountId(goodsId).orElseThrow(
                ()-> new CustomException(ACCOUNT_IMAGE_NOT_FOUND)
        );
        deleteFile(accountImage.getImageUrl());
    }



    private String getFileExtension(String fileName) throws CustomException {
        String lowerCase = fileName.toLowerCase();
        String target;
        String shortName;
        try {
            target = lowerCase.substring(lowerCase.lastIndexOf("."));
            shortName = lowerCase.substring(target.lastIndexOf("."), lowerCase.lastIndexOf("."));

        } catch (StringIndexOutOfBoundsException e) {
            throw new CustomException(WRONG_FILE_TYPE);
        }
        if (fileList.contains(target)) {
            return "_" + shortName + target;
        } else {
            throw new CustomException(WRONG_FILE_TYPE);
        }
    }
}