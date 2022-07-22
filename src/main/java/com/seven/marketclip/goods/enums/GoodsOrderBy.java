package com.seven.marketclip.goods.enums;

import lombok.Getter;

@Getter
public enum GoodsOrderBy {
    ORDER_BY_CREATED_AT("최신순"),
    ORDER_BY_VIEW_COUNT("조회순"),
    ORDER_BY_WISHLIST_COUNT("인기순");

    private final String order;

    GoodsOrderBy(String order){
        this.order = order;
    }

}
