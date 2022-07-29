package com.seven.marketclip.cloud_server.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface FileCloudService {
    String uploadFile(MultipartFile multipartFile);
    void deleteFile(String fileUrl);
    void scheduledClearance();
}
