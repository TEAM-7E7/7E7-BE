package com.seven.marketclip.chat.eums;

public enum SellStatus {
    //상품 상태
    //SALE
    SELLER_TRY("SELLER_TRY"),                // 판매자  버튼 활성화
    BUYER_WAITING("BUY_WAITING"),                 // 구매자  버튼 없음
    //RESERVED
    TRADE_WAITING("TRADE_WAITING"),         // 판매자  버튼 일시적 비활성화
    BUYER_CHECK_REQUEST("BUYER_CHECK"),     // 구매자에게 확인 요청 확인 O X 버튼 활성화
    BUYER_STANDBY("BUYER_STANDBY"),         // 구매자 선택 안된 사용자
    //SOLDOUT
    SOLD_OUT("SOLD_OUT");                   // 양 쪽 사용자 버튼 비활성화 채팅 비활성화

    private final String status;
    SellStatus(String status) { this.status = status; }

}
//  상품 상태      SALE                   RESERVED                     SOLDOUT
//          판매자     구매자           판매자     구매자             판매자     구매자
//         SELLER_TRY             TRADE_WAITING                 SOLD_OUT
//                    BUYER_TRY            BUYER_CHECK_REQUEST             SOLD_OUT