package com.seven.marketclip.chat.service;


import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.domain.ChatRoom;
import com.seven.marketclip.chat.dto.*;
import com.seven.marketclip.chat.repository.ChatRoomRepository;
import com.seven.marketclip.chat.subpub.RedisPublisher;
import com.seven.marketclip.chat.subpub.RedisSubscriber;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.enums.GoodsStatus;
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
    private final RedisPublisher redisPublisher;
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
        Goods goods = goodsRepository.findById(roomMake.getGoodsId()).orElseThrow(
                ()->new CustomException(GOODS_NOT_FOUND)
        );
        if(goods.getStatus() == GoodsStatus.SOLD_OUT){
            throw new CustomException(SOLD_OUT_GOODS);
        }
        Long room = chatRoomRepository.myRoomFindQuery(
                roomMake.getId(), roomMake.getGoodsId(), loginId);
        if(room != 0L || goods.getAccount().getId() == loginId){// 위 쿼리문 조건 + 내가 나의 채팅방을 만든경우
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

    @Transactional  //채팅방 check box 삭제 API 4번
    public void removeChatRoom(ChatMessageInfo roomInfo, Long loginId){
        ChatRoom room = chatRoomRepository.oneRoomFindQuery(roomInfo.getGoodsId(), roomInfo.getBuyerId()).orElseThrow(
                ()->new CustomException(CHAT_ROOM_NOT_FOUND)
        );
        Long partnerId;
        if(loginId == room.getAccount().getId()){       //로그인 아이디가 구매자 인경우
            partnerId = room.getGoods().getAccount().getId();
            if(!room.getGoods().getGoodsReview().isEmptyAccount()){
                if(room.getGoods().getGoodsReview().getAccount().getId() == loginId &
                                        room.getGoods().getStatus() == GoodsStatus.RESERVED){   //리뷰 써야할 사람이 나가면
                    room.getGoods().updateStatusSale();
                    room.getGoods().getGoodsReview().cancelReview();
                }
            }
        }else{
            partnerId = room.getAccount().getId();
            if(!room.getGoods().getGoodsReview().isEmptyAccount()) {
                if (room.getGoods().getGoodsReview().getAccount().getId() == partnerId &
                        room.getGoods().getStatus() == GoodsStatus.RESERVED) {   //구매 완료 대기 중 방을 나가면
                    room.getGoods().updateStatusSale();
                    room.getGoods().getGoodsReview().cancelReview();
                }
            }
        }

        Long goodsId = room.getGoods().getId();
        chatRoomRepository.deleteById(room.getId());
        redisPublisher.publish(getTopic(room.getId()),
                ChatMessageReq.builder()
                        .chatRoomId("CHAT_REMOVE")
                        .partnerId(partnerId)
                        .message(String.valueOf(goodsId))        //유저가 '삭제된 채팅방' 메시지를 칠 수 있기 때문에
                        .build());
    }

    @Transactional  //채팅방 check box 삭제 API 4번
    public void removeAllChatRoom(Long loginId){
        List<ChatRoom> rooms = chatRoomRepository.roomsFindQuery(loginId);
        Long partnerId;
        for(ChatRoom room:rooms){
            if(loginId == room.getAccount().getId()){
                partnerId = room.getGoods().getAccount().getId();
            }else{
                partnerId = room.getAccount().getId();
            }
            Long goodsId = room.getGoods().getId();
            chatRoomRepository.deleteById(room.getId());
            redisPublisher.publish(getTopic(room.getId()),
                    ChatMessageReq.builder()
                            .chatRoomId("CHAT_REMOVE")
                            .partnerId(partnerId)
                            .message(String.valueOf(goodsId))        //유저가 '삭제된 채팅방' 메시지를 칠 수 있기 때문에
                            .build());
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
    public void sendToPubReview(ChatMessageReq message, String chatRoomId){
        redisPublisher.publish(getTopic(chatRoomId), message);
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

    public ChannelTopic getTopic(String roomId) {return topics.get(roomId);}
}
