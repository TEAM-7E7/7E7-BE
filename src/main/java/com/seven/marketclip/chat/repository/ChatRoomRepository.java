package com.seven.marketclip.chat.repository;

import com.seven.marketclip.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByBuyerIdAndGoodsId(Long buyerId, Long goodsId);
}
