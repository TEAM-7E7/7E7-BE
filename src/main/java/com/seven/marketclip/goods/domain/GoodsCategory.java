package com.seven.marketclip.goods.domain;

import lombok.Getter;


@Getter
public enum GoodsCategory {
    DIGITAL_ELECTRONICS("디지털/가전"),
    FURNITURE_INTERIOR("가구/인테리어"),
    INFANT_BOOK("유아동/유아도서"),
    LIVING_INSTANCE("생활/가공식품"),
    SPORT_LEISURE("스포츠/레저"),
    WOMAN_GOODS("여성잡화"),
    WOMAN_FASHION("여성의류"),
    MAN_FASHION_GOODS("남성패션/잡화");

    private final String type;

    GoodsCategory(String type){
        this.type = type;
    }
}
