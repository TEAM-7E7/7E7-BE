package com.seven.marketclip.chat.eums;

public enum SellStatus {
    //상품 상태
    //SALE
    SELLER_TRY("판매자시도"),                // 판매자  버튼 활성화
    BUYER_TRY("구매자시도"),                 // 구매자  버튼 없음
    //RESERVED
    BUYER_CHECK_REQUEST("구매확인요청"),   // 구매자에게 확인 요청 확인 O X 버튼 활성화
    TRADE_WAITING("거래대기중"),           // 판매자  버튼 일시적 비활성화
    //SOLDOUT
    SOLD_OUT("판매완료");                   // 양 쪽 사용자 버튼 비활성화 채팅 비활성화

    private final String status;
    SellStatus(String status) { this.status = status; }

}
//  상품 상태      SALE                   RESERVED                     SOLDOUT
//          판매자     구매자           판매자     구매자             판매자     구매자
//         SELLER_TRY             TRADE_WAITING                 SOLD_OUT
//                    BUYER_TRY            BUYER_CHECK_REQUEST             SOLD_OUT