package com.seven.marketclip.chat.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
public class ChatMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //채팅방 ID
    private Long chatRoomId;
    //발신 회원
    private Long senderId;
    //내용
    private String message;
    private boolean read = false;  //기본값 false 지정
    //날짜(정렬 기준)
    private Date createdAt;

    @Builder
    public ChatMessages(Long chatRoomId, Long senderId, String message, Date createdAt){
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public void readMessage(){
        this.read = true;
    }
}