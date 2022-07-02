package com.seven.marketclip.goods.dto;

import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.domain.GoodsStatus;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class GoodsResponse {
    private Long id;
    private Integer viewCount;
    private Integer wishCount;
    private String account;
    private String title;
    private String category;
    private String description;
    private Integer sellPrice;
    private String fileUrl;
    private GoodsStatus status;
    private LocalDateTime createdAt;

    public GoodsResponse(Goods goods){
        this.title = goods.getTitle();
        this.id = goods.getId();
        this.account = goods.getAccount().getEmail();
        this.title = goods.getTitle();
        this.category = goods.getCategory();
        this.description = goods.getDescription();
        this.createdAt = goods.getCreatedAt();
        this.fileUrl = goods.getFileUrl();
        this.viewCount = goods.getViewCount();
        this.sellPrice = goods.getSellPrice();
        this.wishCount = goods.getWishLists().size();
        this.status = goods.getStatus();
    }
}
