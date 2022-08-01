package com.seven.marketclip.chat.dto;

import com.seven.marketclip.chat.domain.ChatMessages;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ChatMessagesDto {
    private Long messageId;
    private String chatRoomId;
    private String senderNickname;
    private String message;
    private Date createAt;

    public ChatMessagesDto(ChatMessages chatMessages){
        this.messageId = chatMessages.getId();
        this.chatRoomId = chatMessages.getChatRoomId().getId();
        this.senderNickname = chatMessages.getSenderId().getNickname();
        this.message = chatMessages.getMessage();
        this.createAt = chatMessages.getCreatedAt();
    }
}
