package com.seven.marketclip.chat.controller;

import com.seven.marketclip.chat.dto.ChatMessageInfo;
import com.seven.marketclip.chat.dto.ChatMessageReq;
import com.seven.marketclip.chat.dto.ChatMessagesDto;
import com.seven.marketclip.chat.service.ChatMessageService;
import com.seven.marketclip.chat.service.ChatRoomService;
import com.seven.marketclip.chat.subpub.RedisPublisher;
import com.seven.marketclip.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final RedisPublisher redisPublisher;

    @MessageMapping("/chat/first-message")       // 첫 번째 메세지 전송
    public void firstMessage(ChatMessageReq message) {
        if(!chatRoomService.findChatRoom(message.getGoodsId(), message.getSenderId())){
            chatRoomService.saveChatRoom(message.getGoodsId(), message.getSenderId());
        }
        redisPublisher.publish(chatRoomService.getTopic(message.getChatRoomId()), message);
    }

    // websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message")            //메세지 전송
    public void message(ChatMessageReq message) {
        redisPublisher.publish(chatRoomService.getTopic(message.getChatRoomId()), message);
    }
    @PostMapping("/chat-message-list")       //메세지 전체 내역 불러오기 및 읽음 처리
    public List<ChatMessagesDto> chatMessageList(@RequestBody ChatMessageInfo roomInfo, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatMessageService.messageList(roomInfo.getRoomId(), userDetails.getId());
    }
    @PostMapping("/chat-read-check")       //메세지 읽음 처리
    public String chatReadModify(@RequestBody ChatMessageInfo roomInfo, @AuthenticationPrincipal UserDetailsImpl userDetails){
        chatMessageService.modifyCheckRead(roomInfo.getRoomId(), userDetails.getId());
        return "읽음 처리 완료";
    }
}