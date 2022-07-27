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
    private List<ChatMessagesDto> messages;

    @Builder
    public ChatRoomTwo(String chatRoomId, List<ChatMessagesDto> messages){
        this.chatRoomId = chatRoomId;
        this.messages = messages;
    }
}


