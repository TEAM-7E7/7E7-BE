package com.seven.marketclip.goods.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seven.marketclip.goods.domain.Goods;
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

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "userId")
    @Column(nullable = false)
    private String username;

    @Builder
    public WishLists(Goods goods, String username) {
        this.goods = goods;
        this.username = username;
    }
}