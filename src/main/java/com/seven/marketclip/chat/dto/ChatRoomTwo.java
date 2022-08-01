package com.seven.marketclip.chat.dto;

import com.seven.marketclip.chat.eums.SellStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomTwo implements Serializable {
    private String chatRoomId;
    private String goodsTitle;
    private String myProfileUrl;
    private String partnerProfileUrl;
    private String partnerNickname;
    private Long goodsId;               //추가 거래 완료를 위함
    private Long sellerId;
    private Long buyerId;
    private SellStatus sellStatus;      // 상태에 따른 구매 버튼 변화
    private List<ChatMessagesDto> messages;


    @Builder
    public ChatRoomTwo(String chatRoomId, String goodsTitle, String myProfileUrl, String partnerNickname,
                       String partnerProfileUrl, Long goodsId, Long sellerId, Long buyerId, SellStatus sellStatus,
                       List<ChatMessagesDto> messages) {
        this.goodsTitle = goodsTitle;
        this.chatRoomId = chatRoomId;
        this.myProfileUrl = myProfileUrl;
        this.partnerNickname = partnerNickname;
        this.partnerProfileUrl = partnerProfileUrl;
        this.goodsId = goodsId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.sellStatus = sellStatus;
        this.messages = messages;
    }

}

