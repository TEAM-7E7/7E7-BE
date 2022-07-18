package com.seven.marketclip.goods.enums;

import lombok.Getter;

@Getter
public enum GoodsStatus {
    SALE("판매중인 상품입니다."),
    RESERVED("예약된 상품입니다."),
    SOLD_OUT("판매완료된 상품입니다.");

    private final String detail;

    GoodsStatus(String detail){
        this.detail = detail;
    }

}
