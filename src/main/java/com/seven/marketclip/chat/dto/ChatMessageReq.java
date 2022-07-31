package com.seven.marketclip.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageReq {
    private Long partnerId;
    private String nickName;
    //상품 ID
    private Long goodsId;
    //채팅방 ID
    private String chatRoomId;
    //보낸 사람
    private Long senderId;
    //내용
    private String message;
    private boolean checkRead;
    //날짜(정렬 기준)
    private Date createdAt;
    @Builder
    public ChatMessageReq(Long partnerId, String nickName, Long goodsId, String chatRoomId, Long senderId, String message, boolean checkRead, Date createdAt){
        this.partnerId = partnerId;
        this.nickName = nickName;
        this.goodsId = goodsId;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.checkRead = checkRead;
        this.createdAt = createdAt;
    }
}
