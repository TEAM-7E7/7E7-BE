package com.seven.marketclip.goods.domain;

import com.seven.marketclip.Timestamped;
import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.wishList.domain.WishLists;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Goods extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false, length = 25)
    private String title;//제목

    @Column(nullable = false)
    private String description;//내용

    private GoodsCategory category;

    private Integer sellPrice = 0;

    private GoodsStatus status = GoodsStatus.NEW;

    private Integer viewCount = 0;

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Files> filesList;

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishLists> wishLists;

    @Builder
    public Goods(Long id, Account account, String title, String description, GoodsCategory category, Integer sellPrice) {
        this.id = id;
        this.account = account;
        this.title = title;
        this.description = description;
        this.category = category;
        this.sellPrice = sellPrice;
    }

    @Builder
    public Goods(GoodsReqDTO goodsReqDTO, Account account) {
        this.title = goodsReqDTO.getTitle();
        this.description = goodsReqDTO.getDescription();
        this.sellPrice = goodsReqDTO.getSellPrice();
        this.category = goodsReqDTO.getCategory();
        this.account = account;
    }

    public void update(GoodsReqDTO goodsReqDTO) {
        this.title = goodsReqDTO.getTitle();
        this.description = goodsReqDTO.getDescription();
        this.sellPrice = goodsReqDTO.getSellPrice();
        this.category = goodsReqDTO.getCategory();
    }



}
