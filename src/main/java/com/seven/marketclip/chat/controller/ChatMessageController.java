package com.seven.marketclip.chat.controller;

import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.dto.ChatMessageReq;
import com.seven.marketclip.chat.service.ChatMessageService;
import com.seven.marketclip.chat.service.ChatRoomService;
import com.seven.marketclip.chat.subpub.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    private final RedisPublisher redisPublisher;
    // websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message")
    public void message(ChatMessageReq message) {
        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        redisPublisher.publish(chatRoomService.getTopic(message.getChatRoomId()), message);
    }
    //여기 부터 새로 생성
    @GetMapping("/chat-message/{partnerId}")
    public List<ChatMessages> chatMessageList(@PathVariable Long partnerId){
        return chatMessageService.messageList(partnerId); // 랑 토큰의 유저 ID
    }
    @GetMapping("/read-chk-message/{partnerId}")
    public Long readOrNotDetails(@PathVariable Long partnerId){
        return chatMessageService.findReadOrNot(partnerId);
    }


    private final SimpMessageSendingOperations messagingTemplate;

    @GetMapping("/send")
    public String send(){
        messagingTemplate.convertAndSend("/sub/chat/room/" + 1, "Test Message?");
        return "!";
    }

}