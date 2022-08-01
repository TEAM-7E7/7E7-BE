package com.seven.marketclip.goods.dto;

import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsStatus;
import com.seven.marketclip.image.domain.GoodsImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GoodsTitleResDTO implements Serializable {
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
        String firstImageUrl = null;
        for(GoodsImage goodsImage : goods.getGoodsImages()){
            if(goodsImage.getSequence() == 1){
                firstImageUrl = goodsImage.getImageUrl();
                break;
            }
        }
        this.id = goods.getId();
        this.viewCount = goods.getViewCount();
        this.wishIds = goods.getWishLists().stream().map(wish -> wish.getAccount().getId()).collect(Collectors.toList());
        this.nickname = goods.getAccount().getNickname();
        this.accountImageUrl = goods.getAccount().getProfileImgUrl().getImageUrl();
        this.title = goods.getTitle();
        this.category = goods.getCategory();
        this.sellPrice = goods.getSellPrice();
        this.goodsImageUrl = firstImageUrl;
        this.status = goods.getStatus();
        this.createdAt = goods.getCreatedAt();
    }

}
