package com.seven.marketclip.goods.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seven.marketclip.Timestamped;
import com.seven.marketclip.account.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class WishLists extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goodsId")
    private Goods goods;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountId")
    private Account account;


    @Builder
    public WishLists(Goods goods, Account account) {
        this.goods = goods;
        this.account = account;
    }
}