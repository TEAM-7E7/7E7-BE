package com.seven.marketclip.chat.service;


import com.seven.marketclip.account.Account;
import com.seven.marketclip.chat.domain.ChatRoom;
import com.seven.marketclip.chat.dto.ChatRoomGoods;
import com.seven.marketclip.chat.dto.ChatRoomId;
import com.seven.marketclip.chat.repository.ChatMessageRepository;
import com.seven.marketclip.chat.repository.ChatRoomRepository;
import com.seven.marketclip.chat.subpub.RedisSubscriber;
import com.seven.marketclip.goods.domain.Goods;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomId> opsHashChatRoom;
    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private Map<Long, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }
    @Transactional      //채팅방 생성
    public Long saveChatRoom(Long goodsId, Long buyerId) {
        Account ac = Account.builder()
                .id(buyerId)
                .build();
        Goods gd = Goods.builder()
                .id(goodsId)
                .build();
        ChatRoom chatRoom = ChatRoom.builder()
                .account(ac)
                .goods(gd)
                .build();
        chatRoomRepository.save(chatRoom);
        enterChatRoom(chatRoom.getId());
        ChatRoomId redisRoom = ChatRoomId.builder()
                .buyerId(buyerId)
                .goodsId(goodsId)
                .build();
        opsHashChatRoom.put("CHAT_ROOMS", Long.toString(chatRoom.getId()), redisRoom);
        return chatRoom.getId();
    }

    // 단일 채팅방 조회(채팅방 생성시 로직) API 및 메서드 2번
    @Transactional
    public boolean findChatRoom(Long goodsId, Long buyerId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByAccountIdAndGoodsId(buyerId, goodsId);
        if(chatRoom.isEmpty()) {return false;}
        return true;
    }

    @Transactional  //채팅방 check box 삭제 API 4번
    public void removeChatRoom(List<Long> listChatRoomId){
        for (Long chatRoomId:listChatRoomId) {
            chatRoomRepository.deleteById(chatRoomId);
        }
    }

    @Transactional
    public List<ChatRoomGoods> findChatRooms(Long loginId){
        List<ChatRoom> chatRoomList = chatRoomRepository.roomsFindQuery(loginId);
        List<ChatRoomGoods> respRoomList = new ArrayList<>();
        for (ChatRoom room:chatRoomList) {
            Long partnerId;
            if(room.getAccount().getId() != loginId){
                partnerId = room.getAccount().getId();
            }else{
                partnerId = room.getGoods().getAccount().getId();
            }
            ChatRoomGoods chatRoomGoods = ChatRoomGoods
                    .builder()
                    .chatRoom(room)
                    .chatMessages(chatMessageService.findLastMessage(room.getId()))
                    .loginId(loginId)
                    .checkReadCnt(chatMessageService.findCheckReadCnt(partnerId, room.getId()))
                    .build();
            respRoomList.add(chatRoomGoods);
        }
        Collections.sort(respRoomList, (o1, o2) -> {
            if(o1.getLastDate().after(o2.getLastDate())){ //o1이 o2보다 최근 날짜이면
                return -1;  //o1을 앞으로
            } else if (o1.getLastDate().before(o2.getLastDate())) {
                return 1;   //o1을 뒤로
            }
            return 0;
        });

        return respRoomList;
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
