package com.seven.marketclip.chat.subpub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.dto.ChatMessageReq;
import com.seven.marketclip.chat.service.ChatMessageService;
import com.seven.marketclip.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // redis에서 발행된 데이터를 받아 deserialize
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            // ChatMessage 객채로 맵핑
            ChatMessageReq roomMessage = objectMapper.readValue(publishMessage, ChatMessageReq.class);
            // Websocket 구독자에게 채팅 메시지 Send

            if(roomMessage.getChatRoomId().equals("CHAT_REMOVE")){
                messagingTemplate.convertAndSend("/sub/my-rooms/" + roomMessage.getPartnerId(), roomMessage.getMessage() + "_PARTNER_EXIT");
            } else if (roomMessage.getChatRoomId().equals("TRADE")) {
                String messageSeller = "TRADE";
                String messageBuyer = "TRADE";
                if(roomMessage.getMessage().equals("TRADE_CALL")){
                    messageSeller = "TRADE_CALL_SELLER";
                    messageBuyer = "TRADE_CALL_BUYER";
                } else if(roomMessage.getMessage().equals("TRADE_SUCCESS")){
                    messageSeller = "TRADE_SUCCESS_SELLER";
                    messageBuyer = "TRADE_SUCCESS_BUYER";
                } else if(roomMessage.getMessage().equals("TRADE_FAIL")){
                    messageSeller = "TRADE_FAIL_SELLER";
                    messageBuyer = "TRADE_FAIL_BUYER";
                }
//                messagingTemplate.convertAndSend("/sub/my-rooms/" + roomMessage.getPartnerId(),
//                                                    roomMessage.getGoodsId() + "_" + messageBuyer);
//                messagingTemplate.convertAndSend("/sub/my-rooms/" + roomMessage.getSenderId(),
//                                                    roomMessage.getGoodsId() + "_" + messageSeller);
            } else if (roomMessage.getChatRoomId().equals("TRADE_RELOAD")) {      // 거래 완료시 나머지 사용자 reload
                messagingTemplate.convertAndSend("/sub/my-rooms/" + roomMessage.getPartnerId(), "CHAT_RELOAD");
            } else if (roomMessage.getChatRoomId().equals("CHAT_READ_RELOAD")){
                messagingTemplate.convertAndSend("/sub/my-rooms/" + roomMessage.getPartnerId(), "CHAT_RELOAD");
            }else{
                messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getChatRoomId(), roomMessage);
                messagingTemplate.convertAndSend("/sub/my-rooms/" + roomMessage.getPartnerId(), "CHAT_RELOAD");
                messagingTemplate.convertAndSend("/sub/my-rooms/" + roomMessage.getSenderId(), "CHAT_RELOAD");
                chatMessageService.saveChatMessage(roomMessage);    //DB에 저장 API 5번
            }

//            notificationService.send(roomMessage.getSenderId(), roomMessage.getNickName(), roomMessage.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}