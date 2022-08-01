package com.seven.marketclip.scheduler;

import com.seven.marketclip.cloud_server.service.FileCloudService;
import com.seven.marketclip.cloud_server.service.S3CloudServiceImpl;
import com.seven.marketclip.email.EmailRepository;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Component
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT4H")
public class Scheduler {

    private final EmailRepository emailRepository;
    private final FileCloudService fileCloudService;

    public Scheduler(EmailRepository emailRepository, S3CloudServiceImpl s3CloudService){
        this.emailRepository = emailRepository;
        this.fileCloudService = s3CloudService;
    }

    // 여섯시간마다 폐기된 이메일 데이터 삭제
    @Scheduled(cron = "0 0 0/6 * * *")
    @SchedulerLock(
            name = "email_clearance",
            lockAtLeastFor = "PT4H",
            lockAtMostFor = "PT5H"
    )
    public void emailClearance() {
        emailRepository.deleteAllByExpireDateBefore(LocalDateTime.now());
    }

    // 매일 새벽 2시
    @Scheduled(cron = "0 0 2 * * *")
    @SchedulerLock(
            name = "cloud-storage_clearance",
            lockAtLeastFor = "PT10H",
            lockAtMostFor = "PT11H"
    )
    public void s3Clearance(){
        fileCloudService.scheduledClearance();
    }

}
