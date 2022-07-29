package com.seven.marketclip.chat.repository;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessages, Long> {
    List<ChatMessages> findAllByChatRoomIdOrderByCreatedAtAsc(ChatRoom chatRoomId);
    Long countByChatRoomIdAndSenderIdAndCheckRead(ChatRoom chatRoomId, Account partnerId, Boolean bool);
    @Query("select m from ChatMessages m where m.chatRoomId = :id and " +
            "m.createdAt = (select max(m.createdAt) from ChatMessages m where m.chatRoomId = :id)")
    Optional<ChatMessages> latestMessage(@Param("id") ChatRoom chatRoomId);
    @Modifying
    @Query("update ChatMessages c set c.checkRead = true where c.chatRoomId = :chatRoomId and not c.senderId = :loginId")
    int checkReadFlipOver(ChatRoom chatRoomId, Account loginId);


}