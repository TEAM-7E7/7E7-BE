package com.seven.marketclip.goods.dto;

import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.domain.GoodsCategory;
import com.seven.marketclip.goods.domain.GoodsStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GoodsResTitleDTO {
    private Long id;
    private Integer viewCount;
    private Integer wishCount;
    private String account;
    private String title;
    private GoodsCategory category;
    private String description;
    private Integer sellPrice;
    private String fileUrl;
    private GoodsStatus status;
    private LocalDateTime createdAt;

    @Builder
    public GoodsResTitleDTO(Long id, Integer viewCount, Integer wishCount, String account, String title, GoodsCategory category, String description, Integer sellPrice, String fileUrl, GoodsStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.viewCount = viewCount;
        this.wishCount = wishCount;
        this.account = account;
        this.title = title;
        this.category = category;
        this.description = description;
        this.sellPrice = sellPrice;
        this.fileUrl = fileUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    public GoodsResTitleDTO(Goods goods) {
        String fileUrl = goods.getFilesList().get(0).getFileURL();
        this.title = goods.getTitle();
        this.id = goods.getId();
//        this.account = goods.getAccount().getEmail();
        this.title = goods.getTitle();
        this.category = goods.getCategory();
        this.description = goods.getDescription();
        this.createdAt = goods.getCreatedAt();
        this.fileUrl = fileUrl;
        this.viewCount = goods.getViewCount();
        this.sellPrice = goods.getSellPrice();
        this.wishCount = goods.getWishLists().size();
        this.status = goods.getStatus();
    }

}
