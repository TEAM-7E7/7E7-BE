package com.seven.marketclip.chat.service;


import com.seven.marketclip.account.Account;
import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.domain.ChatRoom;
import com.seven.marketclip.chat.dto.ChatMessageReq;
import com.seven.marketclip.chat.dto.ChatMessagesDto;
import com.seven.marketclip.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    @Transactional
    public String saveChatMessage(ChatMessageReq messages) {
        ChatMessages cm = ChatMessages.builder()
                .chatRoomId(ChatRoom.builder()
                        .id(messages.getChatRoomId())
                        .build())
                .senderId(Account.builder()
                        .id(messages.getSenderId())
                        .build())
                .message(messages.getMessage())
                .createdAt(messages.getCreatedAt())
                .build();
        chatMessageRepository.save(cm);
        return "";
    }
    @Transactional      //채팅방의 메시지 조회 및 내 채팅방의 상대 메시지 읽음 처리
    public List<ChatMessagesDto> messageList(Long roomId,Long loginId) {      //전체 메시지 불러오기
        modifyCheckRead(roomId, loginId);
        List<ChatMessages> chatMessagesList = chatMessageRepository.findAllByChatRoomIdOrderByCreatedAtDesc(
                                                                        ChatRoom.builder().id(roomId).build());
        List<ChatMessagesDto> result = chatMessagesList.stream()
                .map(r -> new ChatMessagesDto(r))
                .collect(Collectors.toList());
        return result;
    }
    @Transactional
    public Long findCheckReadCnt(Long chatRoomId, Long partnerId){   // 안읽은 메시지 가져오기
        return chatMessageRepository.countByChatRoomIdAndSenderIdAndCheckRead(
                ChatRoom.builder().id(chatRoomId).build(),
                Account.builder().id(partnerId).build(),
                false);
    }
    @Transactional
    public ChatMessages findLastMessage(Long chatRoomId){       //마지막 채팅내용
        Optional<ChatMessages> ms = chatMessageRepository.latestMessage(
                ChatRoom.builder().id(chatRoomId).build());
        if(ms.isEmpty()){
            return null;
        }
        return ms.get();
    }
    @Transactional
    public void modifyCheckRead(Long chatRoomId, Long loginId){
        chatMessageRepository.checkReadFlipOver(ChatRoom.builder().id(chatRoomId).build(),
                                                Account.builder().id(loginId).build());
    }

}