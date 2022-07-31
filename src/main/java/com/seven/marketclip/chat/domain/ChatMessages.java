package com.seven.marketclip.chat.domain;

import com.seven.marketclip.account.domain.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
public class ChatMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //채팅방 ID
    @ManyToOne
    @JoinColumn(name="chat_room_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ChatRoom chatRoomId;
    //발신 회원
    @ManyToOne
    @JoinColumn(name="account_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Account senderId;
    //내용
    private String message;
    private boolean checkRead = false;  //기본값 false 지정
    //날짜(정렬 기준)
    private Date createdAt;

    @Builder
    public ChatMessages(ChatRoom chatRoomId, Account senderId, String message, Date createdAt, boolean checkRead){
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
        this.checkRead = checkRead;
    }

}