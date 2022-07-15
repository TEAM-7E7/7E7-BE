package com.seven.marketclip.files.domain;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.domain.Goods;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class GoodsImage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;

//    @Column(nullable = false)
//    private String fileType;

    @Column(nullable = false)
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public GoodsImage(Account account, Goods goods, String imageUrl){
        this.account = account;
        this.goods = goods;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }

    public void updateUrl(String url){
        this.imageUrl = url;
    }

}
