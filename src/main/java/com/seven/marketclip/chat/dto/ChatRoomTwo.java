package com.seven.marketclip.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomTwo {
    private String chatRoomId;
    private String goodsTitle;
    private String myProfileUrl;
    private String partnerProfileUrl;
    private String partnerNickname;
    private List<ChatMessagesDto> messages;

    @Builder
    public ChatRoomTwo(String chatRoomId, String goodsTitle, String myProfileUrl, String partnerNickname, String partnerProfileUrl, List<ChatMessagesDto> messages){
        this.goodsTitle = goodsTitle;
        this.chatRoomId = chatRoomId;
        this.messages = messages;
        this.myProfileUrl = myProfileUrl;
        this.partnerNickname = partnerNickname;
        this.partnerProfileUrl = partnerProfileUrl;
    }
}


