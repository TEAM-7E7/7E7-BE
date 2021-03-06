package com.seven.marketclip.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageReq {
    //상품 ID
    private Long goodsId;
    //채팅방 ID
    private Long chatRoomId;
    //보낸 사람
    private Long senderId;
    //내용
    private String message;
    private boolean checkRead;
    //날짜(정렬 기준)
    private Date createdAt;
}
