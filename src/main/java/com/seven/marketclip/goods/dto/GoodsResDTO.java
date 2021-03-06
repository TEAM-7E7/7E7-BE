package com.seven.marketclip.goods.dto;

import com.seven.marketclip.image.domain.GoodsImage;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class GoodsResDTO {
    private Long id;
    private Integer viewCount;
    private List<Long> wishIds;
    private String nickname;
    private String accountImageUrl;
    private String title;
    private GoodsCategory category;
    private String description;
    private Integer sellPrice;
    private List<Map<String, Object>> imageMapList;
    private GoodsStatus status;
    private LocalDateTime createdAt;

    @Builder
    public GoodsResDTO(Long id, Integer viewCount, List<Long> wishIds, String nickname, String accountImageUrl, String title, GoodsCategory category, String description, Integer sellPrice, List<Map<String, Object>> imageMapList, GoodsStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.viewCount = viewCount;
        this.wishIds = wishIds;
        this.nickname = nickname;
        this.accountImageUrl = accountImageUrl;
        this.title = title;
        this.category = category;
        this.description = description;
        this.sellPrice = sellPrice;
        this.imageMapList = imageMapList;
        this.status = status;
        this.createdAt = createdAt;
    }

    public GoodsResDTO(Goods goods) {
        this.id = goods.getId();
        this.nickname = goods.getAccount().getNickname();
        this.title = goods.getTitle();
        this.category = goods.getCategory();
        this.description = goods.getDescription();
        this.createdAt = goods.getCreatedAt();
        this.viewCount = goods.getViewCount();
        this.sellPrice = goods.getSellPrice();
        this.status = goods.getStatus();
    }

    public void setImageMapList(List<GoodsImage> goodsImages) {
        List<Map<String, Object>> imageMapList = new ArrayList<>();
        for (GoodsImage goodsImage : goodsImages) {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("id", goodsImage.getId());
            tempMap.put("url", goodsImage.getImageUrl());

            imageMapList.add(tempMap);
        }
        this.imageMapList = imageMapList;
    }

    public void setAccountImageUrl(String accountImageUrl) {
        this.accountImageUrl = accountImageUrl;
    }

    public void setWishIds(List<Long> wishIds) {
        this.wishIds = wishIds;
    }

}
