package com.seven.marketclip.chat.service;


import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public String saveChatMessage(ChatMessages messages) {
            
        ChatMessages cr = ChatMessages.builder()
                .chatRoomId(messages.getChatRoomId())
//                .senderId(messages.getSenderId())
                .senderId(1L)
                .message(messages.getMessage())
                .createdAt(messages.getCreatedAt())
                .build();
        chatMessageRepository.save(cr);
        return "성공이유";
    }
    @Transactional
    public List<ChatMessages> messageList(Long partnerId){
        List<ChatMessages> li = chatMessageRepository.findAllBySenderIdAndRead(partnerId, false);
        for (ChatMessages cm:li) {
            cm.readMessage();
        }
        return chatMessageRepository.
                findAllBySenderIdOrSenderIdOrderByCreatedAtDesc(partnerId, partnerId);//수정 필요 partnerId
    }
    @Transactional
    public Long findReadOrNot(Long partnerId){
        return chatMessageRepository.countBySenderIdAndRead(partnerId, false);
    }
}
