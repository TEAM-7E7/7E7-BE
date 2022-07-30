package com.seven.marketclip.chat.service;


import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.domain.ChatRoom;
import com.seven.marketclip.chat.dto.ChatRoomGoods;
import com.seven.marketclip.chat.dto.ChatRoomId;
import com.seven.marketclip.chat.dto.RoomMake;
import com.seven.marketclip.chat.repository.ChatRoomRepository;
import com.seven.marketclip.chat.subpub.RedisSubscriber;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.seven.marketclip.exception.ResponseCode.*;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;
    private final GoodsRepository goodsRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomId> opsHashChatRoom;
    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
        List<ChatRoom> list = chatRoomRepository.findAll();
        for (ChatRoom chat:list) {
            enterChatRoom(chat.getId());
        }
    }
    @Transactional      //채팅방 생성
    public String saveChatRoom(RoomMake roomMake, Long loginId) {
        Optional<Goods> goods = goodsRepository.findById(roomMake.getGoodsId());
        if (goods.isEmpty()){                                         // 게시글이 없는 경우
            throw new CustomException(GOODS_NOT_FOUND);
        }
        Long room = chatRoomRepository.myRoomFindQuery(
                roomMake.getId(), roomMake.getGoodsId(), loginId);
        if(room != 0L || goods.get().getAccount().getId() == loginId){// 위 쿼리문 조건 + 내가 나의 채팅방을 만든경우
            throw new CustomException(CHAT_ROOM_NOT_SAVE);
        }
        Account ac = Account.builder()
                .id(loginId)
                .build();
        Goods gd = Goods.builder()
                .id(roomMake.getGoodsId())
                .build();
        ChatRoom chatRoom = ChatRoom.builder()
                .id(roomMake.getId())
                .account(ac)
                .goods(gd)
                .build();
        chatRoomRepository.save(chatRoom);
        enterChatRoom(roomMake.getId());
        ChatRoomId redisRoom = ChatRoomId.builder()
                .buyerId(loginId)
                .goodsId(roomMake.getGoodsId())
                .build();
        opsHashChatRoom.put("CHAT_ROOMS", chatRoom.getId(), redisRoom);
        return chatRoom.getId();
    }

    // 단일 채팅방 조회(채팅방 생성시 로직) API 및 메서드 2번
    @Transactional
    public boolean findChatRoom(Long goodsId, Long loginId, Long partnerId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.roomFindQuery(goodsId, loginId, partnerId);
        if (chatRoom.isEmpty()) {
            return false;
        }
        return true;
    }

    @Transactional  //채팅방 check box 삭제 API 4번
    public void removeChatRoom(List<String> listChatRoomId){
        for (String chatRoomId:listChatRoomId) {
            chatRoomRepository.deleteById(chatRoomId);
        }
    }

    @Transactional
    public List<ChatRoomGoods> findChatRooms(Long loginId){
        List<ChatRoom> chatRoomList = chatRoomRepository.roomsFindQuery(loginId);
        if(chatRoomList.isEmpty()){
            return new ArrayList<>();
        }
        List<ChatRoomGoods> respRoomList = new ArrayList<>();
        for (ChatRoom room:chatRoomList) {
            Long partnerId;
            if(room.getAccount().getId() != loginId){
                partnerId = room.getAccount().getId();
            }else{
                partnerId = room.getGoods().getAccount().getId();
            }
            ChatMessages message = chatMessageService.findLastMessage(room.getId());
            ChatRoomGoods chatRoomGoods;
            if(message == null){
                chatRoomGoods = ChatRoomGoods
                        .builder()
                        .chatRoom(room)
                        .loginId(loginId)
                        .checkReadCnt(0L)
                        .build();
            }else{
                chatRoomGoods = ChatRoomGoods
                        .builder()
                        .chatRoom(room)
                        .loginId(loginId)
                        .checkReadCnt(chatMessageService.findCheckReadCnt(room.getId(), partnerId))
                        .build();
            }
            respRoomList.add(chatRoomGoods);
        }

        List<ChatRoomGoods> list = respRoomList.stream()
                .sorted(Comparator.comparing(ChatRoomGoods::getLastDate,
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .collect(Collectors.toList());
        return list;
    }

    public void enterChatRoom(String chatRoomId) {
        String parseId = chatRoomId;
        ChannelTopic topic = topics.get(chatRoomId);
        if (topic == null) {
            topic = new ChannelTopic(parseId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(chatRoomId, topic);
        }
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}
