package com.seven.marketclip.goods.dto;

import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsStatus;
import com.seven.marketclip.image.domain.GoodsImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.seven.marketclip.exception.ResponseCode.GOODS_IMAGE_NOT_FOUND;

@Getter
@NoArgsConstructor
public class GoodsResDTO implements Serializable {
    private Long id;
    private Integer viewCount;
    private Integer chatRoomCount;
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
    public GoodsResDTO(Long id, Integer viewCount, Integer chatRoomCount, List<Long> wishIds, Long accountId, String nickname, String accountImageUrl, String title, GoodsCategory category, String description, Integer sellPrice, List<Map<String, Object>> imageMapList, GoodsStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.viewCount = viewCount;
        this.chatRoomCount = chatRoomCount;
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

    public GoodsResDTO(Goods goods) throws CustomException {
        List<Map<String, Object>> mapArrayList = new ArrayList<>();
        Map<Integer, GoodsImage> goodsImageMap = new HashMap<>();

        List<GoodsImage> goodsImages = goods.getGoodsImages();
        if(goodsImages.isEmpty()){
            throw new CustomException(GOODS_IMAGE_NOT_FOUND);
        }
        for (GoodsImage goodsImage : goodsImages) {
            goodsImageMap.put(goodsImage.getSequence(), goodsImage);
        }
        for (int i = 1; i < goodsImageMap.size() + 1; i++) {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("id", goodsImageMap.get(i).getId());
            tempMap.put("url", goodsImageMap.get(i).getImageUrl());
            mapArrayList.add(tempMap);
        }

        this.id = goods.getId();
        this.viewCount = goods.getViewCount();
        this.chatRoomCount = goods.getChatRooms().size();
        this.wishIds = goods.getWishLists().stream().map(wish -> wish.getAccount().getId()).collect(Collectors.toList());
        this.accountId = goods.getAccount().getId();
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
