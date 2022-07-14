package com.seven.marketclip.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRes {
    //채팅방 ID
    private Long chatRoomId;
    //보낸사람
    private String sender;
    //내용
    private String message;
    //읽기 여부
    private boolean read;
    //날짜(정렬 기준)
    private Date createdAt;

}
