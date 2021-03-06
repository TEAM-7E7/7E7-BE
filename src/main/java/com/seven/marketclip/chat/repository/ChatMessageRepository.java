package com.seven.marketclip.chat.repository;

import com.seven.marketclip.chat.domain.ChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessages, Long> {
    List<ChatMessages> findAllByChatRoomIdOrderByCreatedAtDesc(Long roomId);
    Long countBySenderIdAndChatRoomIdAndCheckRead(Long chatRoomId, Long partnerId,Boolean bool);
    @Query("select m from ChatMessages m where m.chatRoomId = :id and " +
            "m.createdAt = (select max(m.createdAt) from ChatMessages m)")
    Optional<ChatMessages> latestMessage(@Param("id") Long chatRoomId);
    @Modifying
    @Query("update ChatMessages c set c.checkRead = true where c.chatRoomId = :roomId and not c.senderId = :loginId")
    int checkReadFlipOver(Long roomId, Long loginId);


}