package com.seven.marketclip.goods.dto;

import com.seven.marketclip.image.domain.GoodsImage;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsStatus;
import com.seven.marketclip.wish.domain.Wish;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GoodsResDTO {
    private Long id;
    private Integer viewCount;
    private List<Long> wishIds;
    private Long accountId;
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
    public GoodsResDTO(Long id, Integer viewCount, List<Long> wishIds, Long accountId, String nickname, String accountImageUrl, String title, GoodsCategory category, String description, Integer sellPrice, List<Map<String, Object>> imageMapList, GoodsStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.viewCount = viewCount;
        this.wishIds = wishIds;
        this.accountId = accountId;
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
        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        for (GoodsImage goodsImage : goods.getGoodsImages()) {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("id", goodsImage.getId());
            tempMap.put("url", goodsImage.getImageUrl());

            mapArrayList.add(tempMap);
        }
        this.id = goods.getId();
        this.viewCount = goods.getViewCount();
        this.wishIds = goods.getWishLists().stream().map(Wish::getId).collect(Collectors.toList());
        this.nickname = goods.getAccount().getNickname();
        this.accountImageUrl = goods.getAccount().getProfileImgUrl().getImageUrl();
        this.title = goods.getTitle();
        this.category = goods.getCategory();
        this.description = goods.getDescription();
        this.sellPrice = goods.getSellPrice();
        this.imageMapList = mapArrayList;
        this.status = goods.getStatus();
        this.createdAt = goods.getCreatedAt();
    }

}
