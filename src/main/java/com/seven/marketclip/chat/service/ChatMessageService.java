package com.seven.marketclip.chat.service;


import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    @Transactional
    public String saveChatMessage(ChatMessages messages) {
        ChatMessages cm = ChatMessages.builder()
                .chatRoomId(messages.getChatRoomId())
                .senderId(messages.getSenderId())
                .message(messages.getMessage())
                .createdAt(messages.getCreatedAt())
                .build();
        chatMessageRepository.save(cm);
        return "";
    }
    @Transactional      //채팅방의 메시지 조회 및 내 채팅방의 상대 메시지 읽음 처리
    public List<ChatMessages> messageList(Long roomId,Long loginId){      //전체 메시지 불러오기
        chatMessageRepository.checkReadFlipOver(roomId, loginId);
        return chatMessageRepository.
                findAllByChatRoomIdOrderByCreatedAtDesc(roomId);
    }
    @Transactional
    public Long findCheckReadCnt(Long chatRoomId, Long partnerId){   // 안읽은 메시지 가져오기
        return chatMessageRepository.countBySenderIdAndChatRoomIdAndCheckRead(chatRoomId, partnerId, false);
    }
    @Transactional
    public ChatMessages findLastMessage(Long chatRoomId){       //마지막 채팅내용
        Optional<ChatMessages> ms = chatMessageRepository.latestMessage(chatRoomId);
        if(ms.isEmpty()){
            return null;
        }
        return ms.get();
    }
    @Transactional
    public int modifyCheckRead(Long chatRoomId, Long loginId){
        return chatMessageRepository.checkReadFlipOver(chatRoomId, loginId);
    }

}