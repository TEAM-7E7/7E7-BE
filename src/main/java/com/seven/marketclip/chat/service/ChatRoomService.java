package com.seven.marketclip.chat.service;


import com.seven.marketclip.chat.domain.ChatRoom;
import com.seven.marketclip.chat.dto.ChatRoomReq;
import com.seven.marketclip.chat.repository.ChatMessageRepository;
import com.seven.marketclip.chat.repository.ChatRoomRepository;
import com.seven.marketclip.chat.subpub.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private Map<Long, ChannelTopic> topics;


    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }
    @Transactional      //채팅방 생성
    public String saveChatRoom(ChatRoomReq req) {
        if(findChatRoom(req.getBuyerId(), req.getGoodsId()) == null){
            ChatRoom cr = ChatRoom.builder()
                    .goodsId(req.getGoodsId())
                    .buyerId(req.getBuyerId())
                    .build();
            chatRoomRepository.save(cr);
            enterChatRoom(cr.getId());
            opsHashChatRoom.put("CHAT_ROOMS", Long.toString(cr.getId()), cr);
        }else{
            return "채팅방 존재";
        }
        return "채팅방 생성 완료";
    }


    // 단일 채팅방 조회(채팅방 생성시 로직) API 및 메서드 2번
    @Transactional
    public ChatRoom findChatRoom(Long buyerId, Long goodsId){
        return chatRoomRepository.findByBuyerIdAndGoodsId(buyerId, goodsId);
    }

    @Transactional  //채팅방 check box 삭제 API 4번
    public void removeChatRoom(List<Long> listChatRoomId){
        for (Long chatRoomId:listChatRoomId) {
            chatRoomRepository.deleteById(chatRoomId);
        }
    }

    public void enterChatRoom(Long roomId) {
        String parseId = Long.toString(roomId);
        ChannelTopic topic = topics.get(roomId);
        if (topic == null) {
            topic = new ChannelTopic(parseId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }
    public ChannelTopic getTopic(Long roomId) {
        return topics.get(roomId);
    }
}
