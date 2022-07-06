package com.seven.marketclip.goods.dto;

import com.seven.marketclip.goods.domain.Files;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.domain.GoodsCategory;
import com.seven.marketclip.goods.domain.GoodsStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class GoodsResDTO {
    public Long id;
    public Integer viewCount;
    public Integer wishCount;
    public String account;
    public String title;
    public GoodsCategory category;
    public String description;
    public Integer sellPrice;
    public List<String> fileUrlList;
    public GoodsStatus status;
    public LocalDateTime createdAt;

    @Builder
    public GoodsResDTO(Long id, Integer viewCount, Integer wishCount, String account, String title, GoodsCategory category, String description, Integer sellPrice, List<String> fileUrlList, GoodsStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.viewCount = viewCount;
        this.wishCount = wishCount;
        this.account = account;
        this.title = title;
        this.category = category;
        this.description = description;
        this.sellPrice = sellPrice;
        this.fileUrlList = fileUrlList;
        this.status = status;
        this.createdAt = createdAt;
    }

    public GoodsResDTO(Goods goods) {
        List<String> filesUrlList = new ArrayList<>();
        for(Files files : goods.getFilesList()){
            filesUrlList.add(files.getFileURL());
        }

        this.title = goods.getTitle();
        this.id = goods.getId();
//        this.account = goods.getAccount().getEmail();
        this.title = goods.getTitle();
        this.category = goods.getCategory();
        this.description = goods.getDescription();
        this.createdAt = goods.getCreatedAt();
        this.fileUrlList = filesUrlList;
        this.viewCount = goods.getViewCount();
        this.sellPrice = goods.getSellPrice();
        this.wishCount = goods.getWishLists().size();
        this.status = goods.getStatus();
    }

    public GoodsResDTO getFirstTitleDTO(Goods goods) {
        List<String> fileUrl = Collections.singletonList(goods.getFilesList().get(0).getFileURL());

        return GoodsResDTO.builder()
                .title(goods.getTitle())
                .id(goods.getId())
//                .account(goods.getAccount().getEmail())
                .title(goods.getTitle())
                .category(goods.getCategory())
                .description(goods.getDescription())
                .createdAt(goods.getCreatedAt())
                .fileUrlList(fileUrl)
                .viewCount(goods.getViewCount())
                .wishCount(goods.getWishCount())
                .sellPrice(goods.getSellPrice())
                .status(goods.getStatus())
                .build();
    }

}
