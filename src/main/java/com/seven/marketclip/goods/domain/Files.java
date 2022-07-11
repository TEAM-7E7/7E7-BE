package com.seven.marketclip.goods.domain;

import com.seven.marketclip.account.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @Column(nullable = false)
    private String fileUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Files(Account account, Goods goods, String fileUrl){
        this.account = account;
        this.goods = goods;
        this.fileUrl = fileUrl;
        this.createdAt = LocalDateTime.now();
    }

}
