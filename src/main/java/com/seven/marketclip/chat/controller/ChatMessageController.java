package com.seven.marketclip.chat.controller;

import com.seven.marketclip.chat.domain.ChatRoom;
import com.seven.marketclip.chat.dto.*;
import com.seven.marketclip.chat.eums.SellStatus;
import com.seven.marketclip.chat.service.ChatMessageService;
import com.seven.marketclip.chat.service.ChatRoomService;
import com.seven.marketclip.chat.subpub.RedisPublisher;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatMessageController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final RedisPublisher redisPublisher;

    // websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message")            //메세지 전송
    public void message(ChatMessageReq message) {
        redisPublisher.publish(chatRoomService.getTopic(message.getChatRoomId()), message);
    }
    @PostMapping("/api/chat-message-list")       //메세지 전체 내역 불러오기 및 읽음 처리
    public ChatRoomTwo chatMessageList(@RequestBody ChatMessageInfo roomInfo, @AuthenticationPrincipal UserDetailsImpl userDetails){
        ChatRoom cr = chatRoomService.findRoom(roomInfo.getGoodsId(), userDetails, roomInfo.getPartnerId());
        return chatMessageService.messageList(cr, userDetails, roomInfo.getPartnerId());
    }


    @PostMapping("/api/chat-read-check")       //메세지 읽음 처리
    public String chatReadModify(@RequestBody ChatMessageInfo roomInfo, @AuthenticationPrincipal UserDetailsImpl userDetails){
        chatMessageService.modifyCheckRead(roomInfo.getChatRoomId(), userDetails.getId());
        return "읽음 처리 완료";
    }
    @PostMapping("/api/read-chk-modify")
    public String checkReadModify(@RequestBody ChatMessageInfo roomInfo){
        chatMessageService.modifyCheckRead(roomInfo.getChatRoomId(), roomInfo.getPartnerId());  //로그인한 아이디로 변경
        return "성공";
    }
}