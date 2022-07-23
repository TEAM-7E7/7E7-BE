package com.seven.marketclip.chat.repository;

import com.seven.marketclip.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByAccountIdAndGoodsId(Long buyerId, Long goodsId);
    @Query("select c from ChatRoom c where c.account.id = :id or c.goods.account.id = :id")
    List<ChatRoom> roomsFindQuery(@Param("id") Long id);
}