package com.seven.marketclip.scheduler;

import com.seven.marketclip.cloud_server.service.FileCloudService;
import com.seven.marketclip.cloud_server.service.S3CloudServiceImpl;
import com.seven.marketclip.email.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class Scheduler {

    private final EmailService emailService;
    private final FileCloudService fileCloudService;

    public Scheduler(EmailService emailService, S3CloudServiceImpl s3CloudService){
        this.emailService = emailService;
        this.fileCloudService = s3CloudService;
    }

    // 두시간마다 폐기된 이메일 데이터 삭제
    @Scheduled(cron = "0 0 0/2 * * *")
    @Transactional
    public void emailClearance() {
        emailService.clearanceEmail();
    }

    // 매일 새벽 2시
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void s3Clearance(){
        fileCloudService.scheduledClearance();
    }

}
