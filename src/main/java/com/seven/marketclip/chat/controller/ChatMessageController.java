package com.seven.marketclip.chat.controller;

import com.seven.marketclip.chat.dto.*;
import com.seven.marketclip.chat.service.ChatMessageService;
import com.seven.marketclip.chat.service.ChatRoomService;
import com.seven.marketclip.chat.subpub.RedisPublisher;
import com.seven.marketclip.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatMessageController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final RedisPublisher redisPublisher;

//    @MessageMapping("/chat/first-message")       // 첫 번째 메세지 전송
//    public String firstMessage(ChatMessageReq message) {
//        String chatRoomId = null;
//        if(!chatRoomService.findChatRoom(message.getGoodsId(), message.getSenderId())){
//            chatRoomId = chatRoomService.saveChatRoom(message.getGoodsId(), message.getSenderId());
//        }
//        redisPublisher.publish(chatRoomService.getTopic(chatRoomId), message);
//        return chatRoomId;
//    }

    // websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message")            //메세지 전송
    public void message(ChatMessageReq message) {
        redisPublisher.publish(chatRoomService.getTopic(message.getChatRoomId()), message);
    }
    @PostMapping("/api/chat-message-list")       //메세지 전체 내역 불러오기 및 읽음 처리
//    @Cacheable(key = "#userDetails.id", cacheNames = "chatMessageCache")
    public ChatRoomTwo chatMessageList(@RequestBody ChatRoomReq roomInfo, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatMessageService.messageList(roomInfo.getGoodsId(), userDetails, roomInfo.getPartnerId());
    }
    @PostMapping("/api/chat-read-check")       //메세지 읽음 처리
    public String chatReadModify(@RequestBody ChatMessageInfo roomInfo, @AuthenticationPrincipal UserDetailsImpl userDetails){
        chatMessageService.modifyCheckRead(roomInfo.getChatRoomId(), userDetails.getId());
        return "읽음 처리 완료";
    }

    //테스트 후 삭제
    @GetMapping("/api/read-chk-cnt")
    public Long checkReadCntDetails(@RequestBody ChatMessageInfo roomInfo){
        return chatMessageService.findCheckReadCnt(roomInfo.getChatRoomId(), roomInfo.getPartnerId());
    }
    @PostMapping("/api/read-chk-modify")
    public String checkReadModify(@RequestBody ChatMessageInfo roomInfo){
        chatMessageService.modifyCheckRead(roomInfo.getChatRoomId(), roomInfo.getPartnerId());  //로그인한 아이디로 변경
        return "성공";
    }
}