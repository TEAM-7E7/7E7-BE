package com.seven.marketclip.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomReq {
    private Long goodsId;
    private Long buyerId;
    private Long partnerId;
}
