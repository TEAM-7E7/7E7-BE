package com.seven.marketclip.goods.domain;

import com.seven.marketclip.Timestamped;
import com.seven.marketclip.account.Account;
import com.seven.marketclip.goods.dto.GoodsReqDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Entity
@Validated
@NoArgsConstructor
public class Goods extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @Nullable
    private Account account;

    @Column(nullable = false, length = 25)
//    @NotBlank(message = "제목을 입력하세요")
    private String title;//제목

    @Column(nullable = false)
//    @NotBlank(message = "내용을 입력하세요")
    private String description;//내용

    private GoodsCategory category;

    private Integer sellPrice = 0;

    private GoodsStatus status = GoodsStatus.NEW;

    private Integer viewCount = 0;

    private Integer wishCount = 0;

    @Column
    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishLists> wishLists;

    //    @Column(nullable = false)
    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL)
    private List<Files> filesList;


    @Builder
    public Goods(Account account, String title, String description, GoodsCategory category, Integer sellPrice) {
        this.account = account;
        this.title = title;
        this.description = description;
        this.category = category;
        this.sellPrice = sellPrice;
    }

    @Builder
    public Goods(GoodsReqDTO form, Account account) {
        this.title = form.getTitle();
        this.description = form.getDescription();
        this.sellPrice = form.getSellPrice();
        this.category = form.getCategory();
        this.account = account;
    }

    public void update(GoodsReqDTO form) {
        this.title = form.getTitle();
        this.description = form.getDescription();
        this.sellPrice = form.getSellPrice();
        this.category = form.getCategory();
    }

}
