package com.seven.marketclip.goods.dto;

import com.seven.marketclip.goods.enums.GoodsCategory;
import com.seven.marketclip.goods.enums.GoodsOrderBy;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderByDTO {
    private List<GoodsCategory> goodsCategoryList;
    private GoodsOrderBy goodsOrderBy;

    @Builder
    public OrderByDTO(List<GoodsCategory> goodsCategoryList, GoodsOrderBy goodsOrderBy){
        this.goodsCategoryList = goodsCategoryList;
        this.goodsOrderBy = goodsOrderBy;
    }

}
