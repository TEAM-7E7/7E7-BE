package com.seven.marketclip.goods.dto;

import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class GoodsTitleResDTO {
    private Long id;
    private Integer viewCount;
    private List<Long> wishIds;
    private String nickname;
    private String accountImageUrl;
    private String title;
    private GoodsCategory category;
    private Integer sellPrice;
    private String goodsImageUrl;
    private GoodsStatus status;
    private LocalDateTime createdAt;

    @Builder
    public GoodsTitleResDTO(Long id, Integer viewCount, List<Long> wishIds, String nickname, String accountImageUrl, String title, GoodsCategory category, Integer sellPrice, String goodsImageUrl, GoodsStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.viewCount = viewCount;
        this.wishIds = wishIds;
        this.nickname = nickname;
        this.accountImageUrl = accountImageUrl;
        this.title = title;
        this.category = category;
        this.sellPrice = sellPrice;
        this.goodsImageUrl = goodsImageUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    public GoodsTitleResDTO(Goods goods) {
        String goodsFirstImageUrl;
        if(goods.getGoodsImages().isEmpty()){
            goodsFirstImageUrl = null;
        } else {
            goodsFirstImageUrl = goods.getGoodsImages().get(0).getImageUrl();
        }
        this.id = goods.getId();
        this.nickname = goods.getAccount().getNickname();
        this.title = goods.getTitle();
        this.category = goods.getCategory();
        this.createdAt = goods.getCreatedAt();
        this.goodsImageUrl = goodsFirstImageUrl;
        this.viewCount = goods.getViewCount();
        this.sellPrice = goods.getSellPrice();
        this.status = goods.getStatus();
    }

    public void setAccountImageUrl(String accountImageUrl){
        this.accountImageUrl = accountImageUrl;
    }

    public void setWishIds(List<Long> wishIds){
        this.wishIds = wishIds;
    }

}
