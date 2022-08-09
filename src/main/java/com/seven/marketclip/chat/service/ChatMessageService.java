package com.seven.marketclip.chat.service;


import com.seven.marketclip.account.domain.Account;
import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.domain.ChatRoom;
import com.seven.marketclip.chat.dto.ChatMessageReq;
import com.seven.marketclip.chat.dto.ChatMessagesDto;
import com.seven.marketclip.chat.dto.ChatRoomTwo;
import com.seven.marketclip.chat.eums.SellStatus;
import com.seven.marketclip.chat.repository.ChatMessageRepository;
import com.seven.marketclip.chat.repository.ChatRoomRepository;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.enums.GoodsStatus;
import com.seven.marketclip.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.seven.marketclip.exception.ResponseCode.CHAT_MESSAGE_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    @Transactional
    public void saveChatMessage(ChatMessageReq messages) {
        ChatMessages cm;
        if(messages.getMessage().equals("marketclipchatstarter")){
            cm = ChatMessages.builder()
                    .chatRoomId(ChatRoom.builder()
                            .id(messages.getChatRoomId())
                            .build())
                    .senderId(Account.builder()
                            .id(messages.getSenderId())
                            .build())
                    .message(messages.getMessage())
                    .checkRead(true)
                    .createdAt(messages.getCreatedAt())
                    .build();
        }else{
            cm = ChatMessages.builder()
                    .chatRoomId(ChatRoom.builder()
                            .id(messages.getChatRoomId())
                            .build())
                    .senderId(Account.builder()
                            .id(messages.getSenderId())
                            .build())
                    .message(messages.getMessage())
                    .createdAt(messages.getCreatedAt())
                    .build();
        }
        chatMessageRepository.save(cm);
    }
    @Transactional      //채팅방의 메시지 조회 및 내 채팅방의 상대 메시지 읽음 처리
    public ChatRoomTwo messageList(ChatRoom room, UserDetailsImpl userDetails, Long partnerId) throws CustomException {      //전체 메시지 불러오기   //임시 수정

        List<ChatMessages> chatMessagesList = chatMessageRepository.findAllByChatRoomIdOrderByCreatedAtAsc(
                ChatRoom.builder().id(room.getId()).build());
        if (chatMessagesList.isEmpty()) {
            throw new CustomException(CHAT_MESSAGE_NOT_FOUND);
        }
        modifyCheckRead(room.getId(), userDetails.getId()); //메시지 읽음처리

        List<ChatMessagesDto> result = chatMessagesList.stream()
                .map(r -> new ChatMessagesDto(r))
                .collect(Collectors.toList());
        String chatRoomId = result.get(0).getChatRoomId();
        if (chatRoomId == null || chatRoomId.isEmpty()) {
            chatRoomId = "비었습니다.";
        }

        ChatRoomTwo chatRoomTwo;
        if (room.getAccount().getId() == userDetails.getId()) { // 구매자
            chatRoomTwo = ChatRoomTwo.builder()
                    .chatRoomId(chatRoomId)
                    .goodsTitle(chatMessagesList.get(0).getChatRoomId().getGoods().getTitle())
                    .partnerNickname(room.getGoods().getAccount().getNickname())
                    .myProfileUrl(userDetails.getProfileImgUrl())
                    .partnerProfileUrl(room.getGoods().getAccount().getProfileImgUrl().getImageUrl())
                    .goodsId(room.getGoods().getId())
                    .sellerId(room.getGoods().getAccount().getId())
                    .buyerId(room.getAccount().getId())
                    .sellStatus(findChatStatus(room.getGoods(), userDetails.getId(), false))
                    .messages(result)
                    .build();
        } else {                                                // 판매자
            chatRoomTwo = ChatRoomTwo.builder()
                    .chatRoomId(chatRoomId)
                    .goodsTitle(chatMessagesList.get(0).getChatRoomId().getGoods().getTitle())
                    .partnerNickname(room.getAccount().getNickname())
                    .myProfileUrl(userDetails.getProfileImgUrl())
                    .partnerProfileUrl(room.getAccount().getProfileImgUrl().getImageUrl())
                    .goodsId(room.getGoods().getId())
                    .sellerId(room.getGoods().getAccount().getId())
                    .buyerId(room.getAccount().getId())
                    .sellStatus(findChatStatus(room.getGoods(), userDetails.getId(), true))
                    .messages(result)
                    .build();
        }
        return chatRoomTwo;
    }

    public SellStatus findChatStatus(Goods goods, Long loginId, boolean seller){
        SellStatus status = SellStatus.SOLD_OUT;
        if(seller){             //판매자 일때
            if (goods.getStatus() == GoodsStatus.SALE){
                status = SellStatus.SELLER_TRY;
            }else if (goods.getStatus() == GoodsStatus.RESERVED){
                status =  SellStatus.TRADE_WAITING;
            }
        }else{                  //구매자 일때
            if (goods.getStatus() == GoodsStatus.SALE){
                status = SellStatus.BUYER_WAITING;
            }else if (goods.getStatus() == GoodsStatus.RESERVED){
                status = SellStatus.BUYER_STANDBY;
                if (!goods.getGoodsReview().isEmptyAccount()){
                    if(goods.getGoodsReview().getAccount().getId() == loginId){
                        status =  SellStatus.BUYER_CHECK_REQUEST;
                    }
                }
            }
        }

        return status;
    }
    @Transactional
    public Long findCheckReadCnt(String chatRoomId, Long partnerId){   // 안읽은 메시지 개수
        return chatMessageRepository.countByChatRoomIdAndSenderIdAndCheckRead(
                ChatRoom.builder().id(chatRoomId).build(),
                Account.builder().id(partnerId).build(),
                false);
    }
    @Transactional
    public ChatMessages findLastMessage(String chatRoomId){       //마지막 채팅내용
        Optional<ChatMessages> ms = chatMessageRepository.latestMessage(
                ChatRoom.builder().id(chatRoomId).build());
        if(ms.isEmpty()){
            return null;
        }
        return ms.get();
    }
    @Transactional
    public void modifyCheckRead(String chatRoomId, Long loginId){
        chatMessageRepository.checkReadFlipOver(ChatRoom.builder().id(chatRoomId).build(),
                Account.builder().id(loginId).build());
    }

}