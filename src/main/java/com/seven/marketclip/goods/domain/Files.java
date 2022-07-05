package com.seven.marketclip.goods.domain;

import com.seven.marketclip.account.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import java.time.LocalDateTime;

@Validated
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
    @JoinColumn(name = "file_id")
    private Goods goods;

    @Column(nullable = false)
    private String fileURL;

//    @Column(nullable = false)
//    private String fileType;
//
//    @Column(nullable = false)
//    private String fileName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Files(Account account, Goods goods, String fileURL){
        this.account = account;
        this.goods = goods;
        this.fileURL = fileURL;
//        this.fileType = fileType;
//        this.fileName = fileName;
        this.createdAt = LocalDateTime.now();
    }

}
