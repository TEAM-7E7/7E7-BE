package com.seven.marketclip.image.domain;

import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.goods.domain.Goods;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column
    private int sequence;

    @Column(nullable = false)
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public GoodsImage(Account account, Goods goods, String imageUrl, int sequence) {
        this.account = account;
        this.goods = goods;
        this.imageUrl = imageUrl;
        this.sequence = sequence;
        this.createdAt = LocalDateTime.now();
    }

    public void softDeleteGoods() {
        this.goods = null;
    }

    public void updateGoods(Goods goods) {
        this.goods = goods;
    }

    public void updateSequence(int sequence) {
        this.sequence = sequence;
    }

}
