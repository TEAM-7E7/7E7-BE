package com.seven.marketclip.goods.enums;

import lombok.Getter;

@Getter
public enum GoodsOrderBy {
    ORDER_BY_CREATED_AT("최신 순 정렬."),
    ORDER_BY_VIEW_COUNT("조회수 순 정렬."),
    ORDER_BY_WISHLIST_COUNT("인기 순 정렬.");

    private final String order;

    GoodsOrderBy(String order){
        this.order = order;
    }

}
