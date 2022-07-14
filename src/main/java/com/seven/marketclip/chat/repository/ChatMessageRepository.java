package com.seven.marketclip.chat.repository;

import com.seven.marketclip.chat.domain.ChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessages, Long> {
    List<ChatMessages> findAllBySenderIdOrSenderIdOrderByCreatedAtDesc(Long userId, Long partnerId);
    List<ChatMessages> findAllBySenderId(Long partnerId);
    List<ChatMessages> findAllBySenderIdAndRead(Long partnerId, Boolean bool);

    Long countBySenderIdAndRead(Long partnerId,Boolean bool);
}
