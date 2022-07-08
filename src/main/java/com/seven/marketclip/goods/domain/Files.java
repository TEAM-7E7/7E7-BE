package com.seven.marketclip.goods.domain;

import com.seven.marketclip.account.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Files {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    @Nullable
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 차라리 fileUrl을 받고 files에서 분해해서 bucket과 region, fileName을 찾는게 좋을 듯하다
    // 게시글 수정에서 이미 S3서버에 있는 파일을 받은 그대로 url로 전달하는 과정에서 중요도가 바뀜...
    @Builder
    public Files(Account account, Goods goods, String fileUrl){
        this.account = account;
        this.goods = goods;
        this.fileUrl = fileUrl;
        this.bucket = fileUrl.split(".s3.")[0];
        this.region = fileUrl.split(".s3.")[1].split(".amazonaws.com/")[0];
        this.fileName = fileUrl.split(".s3.")[1].split(".amazonaws.com/")[1];
        this.createdAt = LocalDateTime.now();
    }
//    @Builder
//    public Files(Account account, Goods goods, String bucket, String region, String fileName){
//        this.account = account;
//        this.goods = goods;
//        this.bucket = bucket;
//        this.region = region;
//        this.fileName = fileName;
//        this.fileUrl = bucket+".s3."+region+".amazonaws.com/"+ fileName;
//        this.createdAt = LocalDateTime.now();
//    }

    public void updateFilesUrl(String fileUrl){
        this.fileUrl = fileUrl;
        this.bucket = fileUrl.split(".s3.")[0];
        this.region = fileUrl.split(".s3.")[1].split(".amazonaws.com/")[0];
        this.fileName = fileUrl.split(".s3.")[1].split(".amazonaws.com/")[1];
    }

}
