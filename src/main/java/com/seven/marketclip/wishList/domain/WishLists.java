package com.seven.marketclip.wishList.domain;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.domain.Goods;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class WishLists{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public WishLists(Goods goods, Account account){
        this.account = account;
        this.goods = goods;
        this.createdAt = LocalDateTime.now();
    }

}