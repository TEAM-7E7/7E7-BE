package com.seven.marketclip.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomId implements Serializable {
    private Long goodsId;
    private Long buyerId;
    @Builder
    public ChatRoomId(Long goodsId, Long buyerId){
        this.goodsId = goodsId;
        this.buyerId = buyerId;
    }
}
