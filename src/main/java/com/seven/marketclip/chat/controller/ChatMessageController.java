package com.seven.marketclip.chat.controller;

import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.dto.ChatMessageInfo;
import com.seven.marketclip.chat.dto.ChatMessageReq;
import com.seven.marketclip.chat.dto.ChatRoomReq;
import com.seven.marketclip.chat.service.ChatMessageService;
import com.seven.marketclip.chat.service.ChatRoomService;
import com.seven.marketclip.chat.subpub.RedisPublisher;
import com.seven.marketclip.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final RedisPublisher redisPublisher;

    @MessageMapping("/chat/first-message")       // 첫 번째 메세지 전송
    public void firstMessage(ChatMessageReq message, @AuthenticationPrincipal UserDetailsImpl userDetails) {// 접속중아이디
        if(!chatRoomService.findChatRoom(message.getGoodsId(), userDetails.getId())){                     // 하나 접속중인 아이디로 변경
            chatRoomService.saveChatRoom(message.getGoodsId(), userDetails.getId());
        }
        redisPublisher.publish(chatRoomService.getTopic(message.getChatRoomId()), message);
    }

    // websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message")            //메세지 전송
    public void message(ChatMessageReq message) {
        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        redisPublisher.publish(chatRoomService.getTopic(message.getChatRoomId()), message);
    }
    @GetMapping("/chat-message-list")       //메세지 전체 내역 불러오기 및 읽음 처리
    public List<ChatMessages> chatMessageList(@RequestBody ChatMessageInfo roomInfo, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatMessageService.messageList(roomInfo.getRoomId(), userDetails.getId());
    }
    @GetMapping("/read-chk-cnt")
    public Long checkReadCntDetails(@RequestBody ChatMessageInfo roomInfo){
        return chatMessageService.findCheckReadCnt(roomInfo.getRoomId(), roomInfo.getPartnerId());
    }
    @PostMapping("/read-chk-modify")
    public String checkReadModify(@RequestBody ChatMessageInfo roomInfo){
        chatMessageService.modifyCheckRead(roomInfo.getRoomId(), roomInfo.getPartnerId());  //로그인한 아이디로 변경
        return "성공";
    }

}