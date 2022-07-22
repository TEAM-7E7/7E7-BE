package com.seven.marketclip.goods.enums;

import lombok.Getter;

@Getter
public enum GoodsOrderBy {
    ORDER_BY_CREATED_AT("최신순", " order by created_at desc"),
    ORDER_BY_VIEW_COUNT("조회순", " order by view_count desc"),
    ORDER_BY_WISHLIST_COUNT("인기순", " group by goods.id order by count(wish_lists.id) desc");

    private final String order;
    private final String query;

    GoodsOrderBy(String order, String query){
        this.order = order;
        this.query = query;
    }

}
