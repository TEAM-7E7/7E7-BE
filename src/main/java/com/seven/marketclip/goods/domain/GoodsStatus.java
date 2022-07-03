package com.seven.marketclip.goods.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GoodsStatus {
    NEW("새로 등록된 상품입니다."),
    RESERVED("예약 된 상품입니다."),
    SOLD_OUT("판매완료된 상품입니다.");

    private final String detail;
}
