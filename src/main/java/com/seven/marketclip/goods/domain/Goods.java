package com.seven.marketclip.goods.domain;

import com.seven.marketclip.Timestamped;
import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsStatus;
import com.seven.marketclip.image.domain.GoodsImage;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import com.seven.marketclip.wish.domain.Wish;
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

    @Column(nullable = false, length = 20)
    private String title;//제목

    @Column(nullable = false)
    private String description;//내용

    @Enumerated(value = EnumType.STRING)
    private GoodsCategory category;

    private Integer sellPrice = 0;

    @Enumerated(value = EnumType.STRING)
    private GoodsStatus status = GoodsStatus.SALE;

    private Integer viewCount = 0;

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsImage> goodsImages;

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wish> wishLists;

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
        this.status = goodsReqDTO.getStatus();
        this.category = goodsReqDTO.getCategory();
    }

}
